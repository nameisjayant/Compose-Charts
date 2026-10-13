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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * One filled area of an [OverlappingAreaChart]: [values] spaced evenly from the left edge to the
 * right edge, filled down to zero in [color].
 *
 * Give [color] some transparency so the areas behind it show through. Negative and NaN values are
 * drawn as 0.
 */
@Immutable
public data class AreaSeries(
    val label: String,
    val values: List<Float>,
    val color: Color,
)

/** Visual configuration of an [OverlappingAreaChart]. Build one with [OverlappingAreaChartDefaults.style]. */
@Immutable
public data class OverlappingAreaChartStyle(
    /** Width of the line along the top of each area, in the series colour; 0 draws no line. */
    val lineWidth: Dp,
    /** Colour of the line along the bottom of the chart; transparent hides it. */
    val baselineColor: Color,
    val baselineWidth: Dp,
)

public object OverlappingAreaChartDefaults {
    /** A deeper sky blue, see-through so the later area shows behind it. */
    public val FirstColor: Color = Color(0xB34FB8DA)

    /** A pale sky blue, lighter than [FirstColor]. */
    public val SecondColor: Color = Color(0x9988D2EA)

    public val ChartHeight: Dp = 160.dp

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 900, easing = FastOutSlowInEasing)

    public fun style(
        lineWidth: Dp = 0.dp,
        baselineColor: Color = Color.Transparent,
        baselineWidth: Dp = 1.dp,
    ): OverlappingAreaChartStyle = OverlappingAreaChartStyle(
        lineWidth = lineWidth,
        baselineColor = baselineColor,
        baselineWidth = baselineWidth,
    )
}

/**
 * Translucent areas laid over each other, each joining its values with straight lines and filled
 * down to the bottom of the chart, so the series can be compared where they overlap. There are no
 * axes or labels; pair it with a legend.
 *
 * [series] are drawn in order, so the last one is on top. The bottom of the chart is 0; give it a
 * height with [modifier].
 *
 * @param maxValue value drawn at the top of the chart; defaults to the largest value of any series.
 * @param animationSpec how the areas rise from the bottom and morph between data changes;
 *   `null` disables animation.
 */
@Composable
public fun OverlappingAreaChart(
    series: List<AreaSeries>,
    modifier: Modifier = Modifier,
    maxValue: Float? = null,
    style: OverlappingAreaChartStyle = OverlappingAreaChartDefaults.style(),
    animationSpec: AnimationSpec<Float>? = OverlappingAreaChartDefaults.AnimationSpec,
) {
    val target = remember(series, maxValue) { AreaFrame.of(series, maxValue) }
    val currentAnimationSpec by rememberUpdatedState(animationSpec)

    // Each data change animates from whatever is on screen to the new frame.
    val progress = remember { Animatable(if (animationSpec == null) 1f else 0f) }
    var from by remember { mutableStateOf(target.collapsed()) }
    var to by remember { mutableStateOf(target) }

    LaunchedEffect(target) {
        if (target != to) {
            from = from.lerpTo(to, progress.value)
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

    val path = remember { Path() }
    Canvas(modifier = modifier) {
        drawAreas(
            series = series,
            frame = from.lerpTo(to, progress.value),
            style = style,
            path = path,
        )
    }
}

private fun DrawScope.drawAreas(
    series: List<AreaSeries>,
    frame: AreaFrame,
    style: OverlappingAreaChartStyle,
    path: Path,
) {
    if (size.width <= 0f || size.height <= 0f) return
    val lineWidth = style.lineWidth.toPx()
    // Keep the top line inside the chart at the highest value.
    val top = lineWidth / 2
    val span = size.height - top

    frame.values.forEachIndexed { index, values ->
        val color = series.getOrNull(index)?.color ?: return@forEachIndexed
        if (values.isEmpty()) return@forEachIndexed
        // A lone value is drawn as a flat band across the whole width.
        val points = if (values.size == 1) listOf(values[0], values[0]) else values
        path.rewind()
        points.forEachIndexed { point, value ->
            val x = OverlappingAreaGeometry.x(point, points.size, size.width)
            val y = size.height - OverlappingAreaGeometry.fraction(value, frame.max) * span
            if (point == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        if (lineWidth > 0f) {
            drawPath(path, color.copy(alpha = 1f), style = Stroke(width = lineWidth, join = StrokeJoin.Round))
        }
        path.lineTo(size.width, size.height)
        path.lineTo(0f, size.height)
        path.close()
        drawPath(path, color)
    }

    if (style.baselineColor.alpha > 0f) {
        val baseline = style.baselineWidth.toPx()
        drawRect(
            color = style.baselineColor,
            topLeft = Offset(0f, size.height - baseline),
            size = Size(size.width, baseline),
        )
    }
}

/** Where the values of an [OverlappingAreaChart] sit, kept apart from drawing so it can be tested. */
internal object OverlappingAreaGeometry {
    /** Horizontal position of value [index] of [count], spread from 0 to [width]; a lone value sits at the left edge. */
    fun x(index: Int, count: Int, width: Float): Float =
        if (count <= 1) 0f else width * index / (count - 1)

    /** The value drawn at the top: [maxValue] if given, otherwise the largest value of any series. */
    fun scale(values: List<List<Float>>, maxValue: Float?): Float =
        maxValue?.let(::sanitize) ?: (values.maxOfOrNull { it.maxOrNull() ?: 0f } ?: 0f)

    /** Height of [value] as a share of the chart, clamped to the chart. */
    fun fraction(value: Float, max: Float): Float = if (max <= 0f) 0f else (value / max).coerceIn(0f, 1f)

    fun sanitize(value: Float): Float = if (value.isNaN()) 0f else value.coerceAtLeast(0f)
}

/** The values of every series and the scale shown at one end of an animation. */
internal data class AreaFrame(
    val values: List<List<Float>>,
    val max: Float,
) {
    /** Same scale with every value at zero, the starting point of the entry animation. */
    fun collapsed(): AreaFrame = copy(values = values.map { series -> series.map { 0f } })

    /** Takes on [target]'s shape; values missing on either side rise from zero. */
    fun lerpTo(target: AreaFrame, fraction: Float): AreaFrame = AreaFrame(
        values = target.values.mapIndexed { index, series ->
            val start = values.getOrNull(index).orEmpty()
            series.mapIndexed { point, value -> lerp(start.getOrElse(point) { 0f }, value, fraction) }
        },
        max = lerp(if (max > 0f) max else target.max, target.max, fraction),
    )

    companion object {
        fun of(series: List<AreaSeries>, maxValue: Float?): AreaFrame {
            val values = series.map { it.values.map(OverlappingAreaGeometry::sanitize) }
            return AreaFrame(values, OverlappingAreaGeometry.scale(values, maxValue))
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

@Preview(showBackground = true)
@Composable
private fun OverlappingAreaChartPreview() {
    OverlappingAreaChart(
        series = listOf(
            AreaSeries(
                label = "2011",
                values = listOf(78f, 40f, 26f, 33f, 0f, 19f, 37f, 48f, 60f, 18f, 18f, 66f, 41f, 49f, 21f, 49f, 0f),
                color = OverlappingAreaChartDefaults.FirstColor,
            ),
            AreaSeries(
                label = "2012",
                values = listOf(28f, 98f, 70f, 78f, 60f, 10f, 61f, 46f, 78f, 18f, 18f, 88f, 40f, 59f, 80f, 60f, 26f),
                color = OverlappingAreaChartDefaults.SecondColor,
            ),
        ),
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .height(OverlappingAreaChartDefaults.ChartHeight),
    )
}
