package com.nameisjayant.charts.line

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/** Visual configuration of a [SplineAreaChart]. Build one with [SplineAreaChartDefaults.style]. */
@Immutable
public data class SplineAreaChartStyle(
    val lineColor: Color,
    val lineWidth: Dp,
    /** 0 joins values with straight lines; 1 gives a fully rounded curve. */
    val smoothing: Float,
    /** Fill just under the highest point of the line, fading to [fillBottomColor]. */
    val fillTopColor: Color,
    val fillBottomColor: Color,
    /** Share of the chart's height left empty below the lowest value, so the fill has room to fade. */
    val bottomSpaceFraction: Float,
)

public object SplineAreaChartDefaults {
    public val LineColor: Color = Color(0xFF4499C8)
    public val FillTopColor: Color = Color(0x2E2E8C8C)
    public val FillBottomColor: Color = Color(0x052E8C8C)
    public val ChartHeight: Dp = 170.dp

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 1100, easing = FastOutSlowInEasing)

    public fun style(
        lineColor: Color = LineColor,
        lineWidth: Dp = 4.dp,
        smoothing: Float = 1f,
        fillTopColor: Color = FillTopColor,
        fillBottomColor: Color = FillBottomColor,
        bottomSpaceFraction: Float = 0.45f,
    ): SplineAreaChartStyle = SplineAreaChartStyle(
        lineColor = lineColor,
        lineWidth = lineWidth,
        smoothing = smoothing,
        fillTopColor = fillTopColor,
        fillBottomColor = fillBottomColor,
        bottomSpaceFraction = bottomSpaceFraction,
    )
}

/**
 * A thick smooth line through [values], spaced evenly from the left edge to the right edge, over
 * a gradient fill reaching down to the bottom. There are no axes or labels, so it suits a trend
 * under a headline figure, or a card it bleeds to the edges of.
 *
 * @param minValue value drawn at the lowest point of the line's band; defaults to the smallest value.
 * @param maxValue value drawn at the top of the chart; defaults to the largest value.
 * @param animationSpec how the line sweeps in from the left and morphs between data changes;
 *   `null` disables animation. A change in the number of values sweeps the line in again.
 */
@Composable
public fun SplineAreaChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    minValue: Float? = null,
    maxValue: Float? = null,
    style: SplineAreaChartStyle = SplineAreaChartDefaults.style(),
    animationSpec: AnimationSpec<Float>? = SplineAreaChartDefaults.AnimationSpec,
) {
    val target = remember(values, minValue, maxValue) { SplineFrame.of(values, minValue, maxValue) }
    val currentAnimationSpec by rememberUpdatedState(animationSpec)

    // `from == null` means the line is sweeping in; otherwise it morphs from `from` to `to`.
    val progress = remember { Animatable(if (animationSpec == null) 1f else 0f) }
    var from by remember { mutableStateOf<SplineFrame?>(null) }
    var to by remember { mutableStateOf(target) }

    LaunchedEffect(target) {
        if (target != to) {
            val shown = from?.lerpTo(to, progress.value) ?: to
            from = if (shown.values.size == target.values.size) shown else null
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

    val paths = remember { SplinePaths() }
    Canvas(modifier = modifier) {
        val morphing = from
        drawSplineArea(
            frame = morphing?.lerpTo(to, progress.value) ?: to,
            reveal = if (morphing == null) progress.value else 1f,
            style = style,
            paths = paths,
        )
    }
}

private fun DrawScope.drawSplineArea(
    frame: SplineFrame,
    reveal: Float,
    style: SplineAreaChartStyle,
    paths: SplinePaths,
) {
    if (frame.values.isEmpty() || reveal <= 0f || size.width <= 0f || size.height <= 0f) return
    val lineWidth = style.lineWidth.toPx()
    // The curve may bulge a little past its highest value, so keep a line's width clear above it.
    val top = lineWidth
    val lowest = size.height * (1f - style.bottomSpaceFraction.coerceIn(0f, 1f)) - lineWidth / 2
    val span = frame.max - frame.min
    val points = frame.values.mapIndexed { index, value ->
        Offset(
            x = SplineAreaGeometry.x(index, frame.values.size, size.width),
            y = lowest - (value - frame.min) / span * (lowest - top),
        )
    }
    paths.build(points, style.smoothing.coerceIn(0f, 1f), bottom = size.height)

    clipRect(right = size.width * reveal) {
        drawPath(
            path = paths.area,
            brush = Brush.verticalGradient(
                colors = listOf(style.fillTopColor, style.fillBottomColor),
                startY = points.minOf { it.y },
                endY = size.height,
            ),
        )
        drawPath(
            path = paths.line,
            color = style.lineColor,
            style = Stroke(width = lineWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/** Reused path objects for one chart, rebuilt from the on-screen points every frame. */
private class SplinePaths {
    val line = Path()
    val area = Path()

    /** Joins [points] with Catmull-Rom curves; [area] is the same curve closed along [bottom]. */
    fun build(points: List<Offset>, smoothing: Float, bottom: Float) {
        line.rewind()
        area.rewind()
        val first = points.first()
        line.moveTo(first.x, first.y)
        for (index in 0 until points.lastIndex) {
            val previous = points[max(index - 1, 0)]
            val start = points[index]
            val end = points[index + 1]
            val next = points[minOf(index + 2, points.lastIndex)]
            val control1 = start + (end - previous) * (smoothing / 6f)
            val control2 = end - (next - start) * (smoothing / 6f)
            line.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
        }
        area.addPath(line)
        area.lineTo(points.last().x, bottom)
        area.lineTo(first.x, bottom)
        area.close()
    }
}

/** Where the values of a [SplineAreaChart] sit, kept apart from drawing so it can be tested. */
internal object SplineAreaGeometry {
    /** Horizontal position of value [index] of [count], spread from 0 to [width]; a lone value sits in the middle. */
    fun x(index: Int, count: Int, width: Float): Float =
        if (count <= 1) width / 2 else width * index / (count - 1)

    /** The span the line is scaled to: the overrides where given, otherwise the data's own extent. */
    fun valueRange(values: List<Float>, minValue: Float?, maxValue: Float?): Pair<Float, Float> {
        val low = minValue ?: values.minOrNull() ?: 0f
        val high = maxValue ?: values.maxOrNull() ?: 1f
        // A flat line, or a reversed range, is drawn through the middle of a unit span.
        return if (high > low) low to high else (low - 0.5f) to (low + 0.5f)
    }
}

/** The values and their span shown at one end of an animation. */
private data class SplineFrame(
    val values: List<Float>,
    val min: Float,
    val max: Float,
) {
    /** Only valid when [target] has as many values as this frame. */
    fun lerpTo(target: SplineFrame, fraction: Float): SplineFrame = SplineFrame(
        values = values.zip(target.values) { start, stop -> lerp(start, stop, fraction) },
        min = lerp(min, target.min, fraction),
        max = lerp(max, target.max, fraction),
    )

    companion object {
        fun of(values: List<Float>, minValue: Float?, maxValue: Float?): SplineFrame {
            val clean = values.filter { it.isFinite() }
            val (low, high) = SplineAreaGeometry.valueRange(clean, minValue, maxValue)
            return SplineFrame(values = clean, min = low, max = high)
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

@Preview(showBackground = true)
@Composable
private fun SplineAreaChartPreview() {
    SplineAreaChart(
        values = listOf(180f, 148f, 194f, 184f, 255f, 120f, 200f, 248f, 194f, 200f, 140f, 196f, 192f, 248f),
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .height(SplineAreaChartDefaults.ChartHeight),
    )
}
