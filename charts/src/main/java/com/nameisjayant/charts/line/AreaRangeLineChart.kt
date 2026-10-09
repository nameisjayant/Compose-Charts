package com.nameisjayant.charts.line

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/** A point in data space: [x] along the horizontal axis, [y] along the vertical axis. */
@Immutable
public data class LinePoint(
    val x: Float,
    val y: Float,
)

/**
 * The points of an [AreaRangeLineChart], joined in list order, so the line may run in any
 * direction, including top to bottom as in an altitude profile.
 *
 * [label] is what a legend shows. Points with a NaN or infinite coordinate are skipped.
 */
@Immutable
public data class AreaRangeLineSeries(
    val label: String,
    val points: List<LinePoint>,
    val lineColor: Color,
    val bandColor: Color,
    val pointColor: Color,
)

/**
 * The span of one chart axis: values from [min] to [max], with a tick, label and grid line every
 * [step] starting at [min].
 */
@Immutable
public data class AxisRange(
    val min: Float,
    val max: Float,
    val step: Float,
) {
    init {
        require(max > min) { "max ($max) must be greater than min ($min)" }
        require(step > 0f) { "step ($step) must be positive" }
    }

    internal fun ticks(): List<Float> {
        val count = ((max - min) / step + TickTolerance).toInt().coerceAtMost(MaxTicks)
        return List(count + 1) { min + step * it }
    }

    internal fun lerpTo(target: AxisRange, fraction: Float): AxisRange = AxisRange(
        min = lerp(min, target.min, fraction),
        max = lerp(max, target.max, fraction),
        step = target.step,
    )

    internal companion object {
        private const val TickTolerance = 1e-3f
        private const val MaxTicks = 100

        /** A range covering [low]..[high] with a round step (1, 2, 2.5 or 5 × 10ⁿ). */
        fun nice(low: Float, high: Float, targetSteps: Int = 4): AxisRange {
            val (start, end) = if (high > low) low to high else (low - 1f) to (low + 1f)
            val rawStep = (end - start) / targetSteps
            val magnitude = 10f.pow(floor(log10(rawStep)))
            val step = when (val normalized = rawStep / magnitude) {
                in 0f..1f -> 1f
                in 1f..2f -> 2f
                in 2f..2.5f -> 2.5f
                in 2.5f..5f -> 5f
                else -> 10f
            } * magnitude
            // `+ 0f` turns -0.0 into 0.0.
            return AxisRange(min = floor(start / step) * step + 0f, max = ceil(end / step) * step + 0f, step = step)
        }
    }
}

/** Visual configuration of an [AreaRangeLineChart]. Build one with [AreaRangeLineChartDefaults.style]. */
@Immutable
public data class AreaRangeLineChartStyle(
    val lineWidth: Dp,
    val bandWidth: Dp,
    val bandFadeLength: Dp,
    val pointRadius: Dp,
    val showPoints: Boolean,
    val smoothing: Float,
    val showVerticalGridLines: Boolean,
    val showHorizontalGridLines: Boolean,
    val gridLineColor: Color,
    val gridLineWidth: Dp,
    val showAxisLabels: Boolean,
    val labelTextStyle: TextStyle,
    val labelSpacing: Dp,
)

public object AreaRangeLineChartDefaults {
    public val LineColor: Color = Color(0xFF0B0B5C)
    public val BandColor: Color = Color(0xFF62D3FB)
    public val PointColor: Color = Color(0xFF3B8AC4)
    public val GridLineColor: Color = Color(0xFFECEDF4)
    public val ChartHeight: Dp = 260.dp

    public val LabelTextStyle: TextStyle = TextStyle(
        color = Color(0xFF111111),
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp,
    )

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 1100, easing = FastOutSlowInEasing)

    /**
     * @param bandWidth thickness of the soft ribbon drawn along the line.
     * @param bandFadeLength how far the ribbon fades in from each of its ends.
     * @param smoothing 0 joins points with straight lines; 1 gives a fully rounded curve.
     */
    public fun style(
        lineWidth: Dp = 2.dp,
        bandWidth: Dp = 22.dp,
        bandFadeLength: Dp = 36.dp,
        pointRadius: Dp = 6.dp,
        showPoints: Boolean = true,
        smoothing: Float = 1f,
        showVerticalGridLines: Boolean = true,
        showHorizontalGridLines: Boolean = false,
        gridLineColor: Color = GridLineColor,
        gridLineWidth: Dp = 1.dp,
        showAxisLabels: Boolean = true,
        labelTextStyle: TextStyle = LabelTextStyle,
        labelSpacing: Dp = 10.dp,
    ): AreaRangeLineChartStyle = AreaRangeLineChartStyle(
        lineWidth = lineWidth,
        bandWidth = bandWidth,
        bandFadeLength = bandFadeLength,
        pointRadius = pointRadius,
        showPoints = showPoints,
        smoothing = smoothing,
        showVerticalGridLines = showVerticalGridLines,
        showHorizontalGridLines = showHorizontalGridLines,
        gridLineColor = gridLineColor,
        gridLineWidth = gridLineWidth,
        showAxisLabels = showAxisLabels,
        labelTextStyle = labelTextStyle,
        labelSpacing = labelSpacing,
    )

    /** Whole numbers without a decimal point, anything else rounded to two decimals. */
    public val LabelFormatter: (Float) -> String = { value ->
        val rounded = (value * 100f).roundToLong() / 100.0
        if (rounded % 1.0 == 0.0) rounded.toLong().toString() else rounded.toString()
    }
}

/**
 * A smooth line through [series]' points, wrapped in a wide translucent ribbon that fades out at
 * both ends, with a dot on every point.
 *
 * Both axes are numeric. Points are joined in list order, so the line can run top to bottom (e.g.
 * temperature against altitude) as well as left to right.
 *
 * @param xRange span of the horizontal axis; defaults to a round range covering every point.
 * @param yRange the same for the vertical axis.
 * @param xLabelFormatter text for each horizontal-axis tick, e.g. `{ "${it.toInt()}°" }`.
 * @param yLabelFormatter text for each vertical-axis tick.
 * @param animationSpec how the line draws in and morphs between data changes; `null` disables
 *   animation. A change in the number of points draws the line in again.
 */
@Composable
public fun AreaRangeLineChart(
    series: AreaRangeLineSeries,
    modifier: Modifier = Modifier,
    style: AreaRangeLineChartStyle = AreaRangeLineChartDefaults.style(),
    xRange: AxisRange? = null,
    yRange: AxisRange? = null,
    xLabelFormatter: (Float) -> String = AreaRangeLineChartDefaults.LabelFormatter,
    yLabelFormatter: (Float) -> String = AreaRangeLineChartDefaults.LabelFormatter,
    animationSpec: AnimationSpec<Float>? = AreaRangeLineChartDefaults.AnimationSpec,
) {
    val target = remember(series.points, xRange, yRange) { LineFrame.of(series.points, xRange, yRange) }
    val currentAnimationSpec by rememberUpdatedState(animationSpec)

    // `from == null` means the line is drawing in; otherwise it morphs from `from` to `to`.
    val progress = remember { Animatable(if (animationSpec == null) 1f else 0f) }
    var from by remember { mutableStateOf<LineFrame?>(null) }
    var to by remember { mutableStateOf(target) }

    LaunchedEffect(target) {
        if (target != to) {
            val shown = from?.lerpTo(to, progress.value) ?: to
            from = if (shown.points.size == target.points.size) shown else null
            to = target
        }
        val spec = currentAnimationSpec
        if (spec == null) {
            progress.snapTo(1f)
        } else {
            progress.snapTo(0f)
            progress.animateTo(1f, spec)
        }
    }

    // Labels only depend on the ticks of the frame being animated to, so measure them once per change.
    val textMeasurer = rememberTextMeasurer()
    val labels = remember(to.xAxis, to.yAxis, style.showAxisLabels, style.labelTextStyle, xLabelFormatter, yLabelFormatter) {
        if (!style.showAxisLabels) {
            AxisLabels.None
        } else {
            AxisLabels(
                x = to.xAxis.ticks().map { it to textMeasurer.measure(xLabelFormatter(it), style.labelTextStyle) },
                y = to.yAxis.ticks().map { it to textMeasurer.measure(yLabelFormatter(it), style.labelTextStyle) },
            )
        }
    }

    val paths = remember { CurvePaths() }
    Canvas(modifier = modifier) {
        val morphing = from
        drawAreaRangeLine(
            frame = morphing?.lerpTo(to, progress.value) ?: to,
            ticks = to,
            reveal = if (morphing == null) progress.value else 1f,
            labels = labels,
            series = series,
            style = style,
            paths = paths,
        )
    }
}

private fun DrawScope.drawAreaRangeLine(
    frame: LineFrame,
    ticks: LineFrame,
    reveal: Float,
    labels: AxisLabels,
    series: AreaRangeLineSeries,
    style: AreaRangeLineChartStyle,
    paths: CurvePaths,
) {
    val halfBand = style.bandWidth.toPx() / 2
    val labelSpacing = style.labelSpacing.toPx()

    // Plot area: inside the label gutters, inset so the ribbon and edge labels stay on canvas.
    val plot = if (labels.x.isEmpty() && labels.y.isEmpty()) {
        Rect(halfBand, halfBand, size.width - halfBand, size.height - halfBand)
    } else {
        val yLabelWidth = labels.y.maxOfOrNull { it.second.size.width }?.toFloat() ?: 0f
        val yLabelHalfHeight = (labels.y.maxOfOrNull { it.second.size.height } ?: 0) / 2f
        val xLabelHeight = labels.x.maxOfOrNull { it.second.size.height }?.toFloat() ?: 0f
        val firstXHalf = (labels.x.firstOrNull()?.second?.size?.width ?: 0) / 2f
        val lastXHalf = (labels.x.lastOrNull()?.second?.size?.width ?: 0) / 2f
        Rect(
            left = max(yLabelWidth + labelSpacing, firstXHalf),
            top = max(halfBand, yLabelHalfHeight),
            right = size.width - max(halfBand, lastXHalf),
            bottom = size.height - xLabelHeight - labelSpacing,
        )
    }
    if (plot.width <= 0f || plot.height <= 0f) return

    val xAxis = frame.xAxis
    val yAxis = frame.yAxis
    fun screenX(x: Float): Float = plot.left + (x - xAxis.min) / (xAxis.max - xAxis.min) * plot.width
    fun screenY(y: Float): Float = plot.bottom - (y - yAxis.min) / (yAxis.max - yAxis.min) * plot.height

    val gridWidth = style.gridLineWidth.toPx()
    if (style.showVerticalGridLines) {
        ticks.xAxis.ticks().forEach { tick ->
            val x = screenX(tick)
            if (x in plot.left - 0.5f..plot.right + 0.5f) {
                drawLine(style.gridLineColor, Offset(x, plot.top), Offset(x, plot.bottom), gridWidth)
            }
        }
    }
    if (style.showHorizontalGridLines) {
        ticks.yAxis.ticks().forEach { tick ->
            val y = screenY(tick)
            if (y in plot.top - 0.5f..plot.bottom + 0.5f) {
                drawLine(style.gridLineColor, Offset(plot.left, y), Offset(plot.right, y), gridWidth)
            }
        }
    }
    labels.y.forEach { (tick, layout) ->
        val y = screenY(tick)
        if (y in plot.top - 0.5f..plot.bottom + 0.5f) {
            drawText(layout, topLeft = Offset(0f, y - layout.size.height / 2f))
        }
    }
    labels.x.forEach { (tick, layout) ->
        val x = screenX(tick)
        if (x in plot.left - 0.5f..plot.right + 0.5f) {
            drawText(layout, topLeft = Offset(x - layout.size.width / 2f, plot.bottom + labelSpacing))
        }
    }

    val points = frame.points.map { Offset(screenX(it.x), screenY(it.y)) }
    if (points.isEmpty() || reveal <= 0f) return

    // The ribbon runs half its width past the first and last points so their dots sit inside it.
    val extension = if (points.size > 1) halfBand else 0f
    paths.build(points, style.smoothing, extension)
    val bandLength = paths.bandMeasure.length
    val revealed = bandLength * reveal

    if (revealed > 0f) {
        paths.segment.rewind()
        paths.bandMeasure.getSegment(0f, revealed, paths.segment)
        // The ribbon can poke past the canvas edges, so the layer must not clip it.
        drawIntoCanvas { it.saveLayer(Rect(Offset.Zero, size).inflate(halfBand * 3), Paint()) }
        drawPath(
            path = paths.segment,
            color = series.bandColor,
            style = Stroke(width = halfBand * 2, cap = StrokeCap.Butt, join = StrokeJoin.Round),
        )
        val fade = style.bandFadeLength.toPx()
        if (fade > 0f) {
            fadeBandEnd(paths.bandMeasure, distance = 0f, inward = 1f, fade = fade, halfBand = halfBand)
            fadeBandEnd(paths.bandMeasure, distance = revealed, inward = -1f, fade = fade, halfBand = halfBand)
        }
        drawIntoCanvas { it.restore() }
    }

    // The line starts at the first point, `extension` into the ribbon, and trails its head by as much.
    val lineRevealed = (revealed - extension).coerceIn(0f, paths.lineMeasure.length)
    if (lineRevealed > 0f) {
        paths.segment.rewind()
        paths.lineMeasure.getSegment(0f, lineRevealed, paths.segment)
        drawPath(
            path = paths.segment,
            color = series.lineColor,
            style = Stroke(width = style.lineWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }

    if (style.showPoints) {
        val radius = style.pointRadius.toPx()
        // Each dot pops in as the ribbon's head reaches it.
        val popDistance = max(radius * 4, 1f)
        points.forEachIndexed { index, point ->
            val scale = when {
                reveal >= 1f -> 1f
                points.size == 1 -> reveal
                else -> ((revealed - extension - paths.pointDistances[index]) / popDistance).coerceIn(0f, 1f)
            }
            if (scale > 0f) drawCircle(series.pointColor, radius = radius * scale, center = point)
        }
    }
}

/**
 * Erases the ribbon towards one end with a gradient, so it fades out instead of stopping square.
 * [inward] is +1 when the ribbon continues forward from [distance] and -1 when it continues back.
 */
private fun DrawScope.fadeBandEnd(measure: PathMeasure, distance: Float, inward: Float, fade: Float, halfBand: Float) {
    val position = measure.getPosition(distance)
    val tangent = measure.getTangent(distance)
    if (!position.isValid() || !tangent.isValid() || tangent == Offset.Zero) return
    val direction = tangent / tangent.getDistance() * inward
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color.Black, Color.Transparent),
            start = position,
            end = position + direction * fade,
        ),
        radius = hypot(fade, halfBand),
        center = position,
        blendMode = BlendMode.DstOut,
    )
}

/** Reused path objects for one chart, rebuilt from the on-screen points every frame. */
private class CurvePaths {
    val line = Path()
    val band = Path()
    val segment = Path()
    val lineMeasure = PathMeasure()
    val bandMeasure = PathMeasure()
    private val scratch = Path()
    private val scratchMeasure = PathMeasure()

    /** Distance along [line] at which each point sits. */
    var pointDistances: FloatArray = FloatArray(0)
        private set

    /** Joins [points] with Catmull-Rom curves; [band] is the same curve extended by [extension] at both ends. */
    fun build(points: List<Offset>, smoothing: Float, extension: Float) {
        line.rewind()
        band.rewind()
        if (pointDistances.size != points.size) pointDistances = FloatArray(points.size)

        // Catmull-Rom curves leave each end heading straight at its neighbour.
        val first = points.first()
        val last = points.last()
        val head = first - (points[minOf(1, points.lastIndex)] - first).normalized() * extension
        val tail = last + (last - points[max(points.lastIndex - 1, 0)]).normalized() * extension
        line.moveTo(first.x, first.y)
        band.moveTo(head.x, head.y)
        band.lineTo(first.x, first.y)
        var distance = 0f
        for (index in 0 until points.lastIndex) {
            val previous = points[max(index - 1, 0)]
            val start = points[index]
            val end = points[index + 1]
            val next = points[minOf(index + 2, points.lastIndex)]
            val control1 = start + (end - previous) * (smoothing / 6f)
            val control2 = end - (next - start) * (smoothing / 6f)
            line.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
            band.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)

            scratch.rewind()
            scratch.moveTo(start.x, start.y)
            scratch.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
            scratchMeasure.setPath(scratch, false)
            distance += scratchMeasure.length
            pointDistances[index + 1] = distance
        }
        band.lineTo(tail.x, tail.y)

        lineMeasure.setPath(line, false)
        bandMeasure.setPath(band, false)
    }

    private fun Offset.normalized(): Offset {
        val length = getDistance()
        return if (length > 0f) this / length else Offset.Zero
    }
}

/** Measured tick labels, each paired with the tick value it labels. */
private class AxisLabels(
    val x: List<Pair<Float, TextLayoutResult>>,
    val y: List<Pair<Float, TextLayoutResult>>,
) {
    companion object {
        val None = AxisLabels(emptyList(), emptyList())
    }
}

/** The points and axis spans shown at one end of an animation. */
private data class LineFrame(
    val points: List<LinePoint>,
    val xAxis: AxisRange,
    val yAxis: AxisRange,
) {
    /** Only valid when [target] has as many points as this frame. */
    fun lerpTo(target: LineFrame, fraction: Float): LineFrame = LineFrame(
        points = points.zip(target.points) { start, stop ->
            LinePoint(x = lerp(start.x, stop.x, fraction), y = lerp(start.y, stop.y, fraction))
        },
        xAxis = xAxis.lerpTo(target.xAxis, fraction),
        yAxis = yAxis.lerpTo(target.yAxis, fraction),
    )

    companion object {
        fun of(points: List<LinePoint>, xRange: AxisRange?, yRange: AxisRange?): LineFrame {
            val clean = points.filter { it.x.isFinite() && it.y.isFinite() }
            return LineFrame(
                points = clean,
                xAxis = xRange ?: AxisRange.nice(clean.minOfOrNull { it.x } ?: 0f, clean.maxOfOrNull { it.x } ?: 1f),
                yAxis = yRange ?: AxisRange.nice(clean.minOfOrNull { it.y } ?: 0f, clean.maxOfOrNull { it.y } ?: 1f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AreaRangeLineChartPreview() {
    AreaRangeLineChart(
        series = AreaRangeLineSeries(
            label = "Temperature",
            points = listOf(
                LinePoint(-77f, 75f),
                LinePoint(-39f, 53f),
                LinePoint(-66f, 24f),
                LinePoint(-40f, 14f),
                LinePoint(-17f, 5f),
            ),
            lineColor = AreaRangeLineChartDefaults.LineColor,
            bandColor = AreaRangeLineChartDefaults.BandColor,
            pointColor = AreaRangeLineChartDefaults.PointColor,
        ),
        xRange = AxisRange(min = -80f, max = -10f, step = 10f),
        yRange = AxisRange(min = 0f, max = 75f, step = 25f),
        yLabelFormatter = { "${it.roundToInt()} km" },
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp)
            .fillMaxWidth()
            .height(AreaRangeLineChartDefaults.ChartHeight),
    )
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction
