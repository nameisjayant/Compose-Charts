package com.nameisjayant.charts.bar

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/** Visual configuration of a [StackedBarChart]. Build one with [StackedBarChartDefaults.style]. */
@Immutable
public data class StackedBarChartStyle(
    /** Gap between neighbouring bars. */
    val barSpacing: Dp,
    /** Widest a bar may get; bars otherwise share the width left after [barSpacing]. */
    val maxBarWidth: Dp,
    /** Rounds the top and bottom of each whole stack; the segments inside meet flush. */
    val cornerRadius: Dp,
)

public object StackedBarChartDefaults {
    public val BottomColor: Color = Color(0xFF03045E)
    public val MiddleColor: Color = Color(0xFF3375B5)
    public val TopColor: Color = Color(0xFFD1EEF6)
    public val ChartHeight: Dp = 130.dp

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 800, easing = FastOutSlowInEasing)

    public fun style(
        barSpacing: Dp = 6.dp,
        maxBarWidth: Dp = Dp.Infinity,
        cornerRadius: Dp = 6.dp,
    ): StackedBarChartStyle = StackedBarChartStyle(
        barSpacing = barSpacing,
        maxBarWidth = maxBarWidth,
        cornerRadius = cornerRadius,
    )
}

/**
 * A bar chart whose bars are stacks: bar `i` piles up value `i` of every [series], the first
 * series at the bottom, each in its own colour.
 *
 * Bars stand on the bottom edge and are spread edge to edge, sharing the width left after
 * [StackedBarChartStyle.barSpacing]. There are no axes or labels.
 *
 * @param maxValue stack total that fills the chart's height; defaults to the tallest stack. Taller
 *   stacks are clipped.
 * @param animationSpec how stacks grow in and between data changes; `null` disables animation.
 */
@Composable
public fun StackedBarChart(
    series: List<BarSeries>,
    modifier: Modifier = Modifier,
    style: StackedBarChartStyle = StackedBarChartDefaults.style(),
    maxValue: Float? = null,
    animationSpec: AnimationSpec<Float>? = StackedBarChartDefaults.AnimationSpec,
) {
    val target = remember(series.map { it.values }, maxValue) { StackFrame.of(series.map { it.values }, maxValue) }
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

    val colors = series.map { it.color }
    val paths = remember { StackPaths() }
    Canvas(modifier = modifier) {
        drawStacks(
            frame = from.lerpTo(to, progress.value),
            colors = colors,
            style = style,
            paths = paths,
        )
    }
}

private fun DrawScope.drawStacks(
    frame: StackFrame,
    colors: List<Color>,
    style: StackedBarChartStyle,
    paths: StackPaths,
) {
    val count = frame.count
    if (count == 0 || frame.max <= 0f || size.width <= 0f || size.height <= 0f) return
    val pxPerUnit = size.height / frame.max
    val barWidth = StackedBarGeometry.barWidth(count, size.width, style.barSpacing.toPx(), style.maxBarWidth.toPx())
    val cornerRadius = style.cornerRadius.toPx()

    for (index in 0 until count) {
        val stackHeight = min(frame.totalAt(index) * pxPerUnit, size.height)
        if (stackHeight <= 0f) continue
        val left = StackedBarGeometry.left(index, count, size.width, barWidth)
        val right = left + barWidth
        val radius = CornerRadius(min(cornerRadius, min(barWidth, stackHeight) / 2))
        paths.stack.rewind()
        paths.stack.addRoundRect(RoundRect(left, size.height - stackHeight, right, size.height, radius))

        // Each segment is cut out of the rounded stack, so only the stack's own ends are rounded.
        var bottom = size.height
        frame.values.forEachIndexed { seriesIndex, values ->
            val height = values.getOrElse(index) { 0f } * pxPerUnit
            if (height <= 0f || bottom <= 0f) return@forEachIndexed
            val top = max(bottom - height, 0f)
            paths.slice.rewind()
            paths.slice.addRect(Rect(left, top, right, bottom))
            paths.segment.op(paths.stack, paths.slice, PathOperation.Intersect)
            drawPath(paths.segment, colors.getOrElse(seriesIndex) { Color.Transparent })
            bottom = top
        }
    }
}

/** Reused path objects for one chart, rebuilt every frame. */
private class StackPaths {
    val stack = Path()
    val slice = Path()
    val segment = Path()
}

/** Where the bars of a [StackedBarChart] sit, kept apart from drawing so it can be tested. */
internal object StackedBarGeometry {
    /** Width of each of [count] bars sharing [width] with [spacing] between them, capped at [maxBarWidth]. */
    fun barWidth(count: Int, width: Float, spacing: Float, maxBarWidth: Float): Float {
        if (count <= 0) return 0f
        val shared = (width - spacing * (count - 1)) / count
        return min(shared, maxBarWidth).coerceAtLeast(0f)
    }

    /** Left edge of bar [index]: the first bar touches the left edge, the last the right; a lone bar is centred. */
    fun left(index: Int, count: Int, width: Float, barWidth: Float): Float =
        if (count <= 1) (width - barWidth) / 2 else (width - barWidth) * index / (count - 1)

    /** Total height of every stack, in values. */
    fun totals(values: List<List<Float>>): List<Float> {
        val count = values.maxOfOrNull { it.size } ?: 0
        return List(count) { index -> values.sumOf { it.getOrElse(index) { 0f }.toDouble() }.toFloat() }
    }
}

/** The segment values (`values[series][bar]`) and scale shown at one end of an animation. */
private data class StackFrame(
    val values: List<List<Float>>,
    val max: Float,
) {
    private val totals: List<Float> = StackedBarGeometry.totals(values)

    val count: Int get() = totals.size

    fun totalAt(index: Int): Float = totals.getOrElse(index) { 0f }

    /** Same scale with every stack at zero height, the starting point of the entry animation. */
    fun collapsed(): StackFrame = copy(values = values.map { series -> series.map { 0f } })

    /** Takes on [target]'s shape; segments missing on either side grow from or shrink to zero. */
    fun lerpTo(target: StackFrame, fraction: Float): StackFrame = StackFrame(
        values = List(target.values.size) { seriesIndex ->
            val start = values.getOrElse(seriesIndex) { emptyList() }
            val stop = target.values[seriesIndex]
            List(stop.size) { lerp(start.getOrElse(it) { 0f }, stop[it], fraction) }
        },
        max = lerp(max, target.max, fraction),
    )

    companion object {
        fun of(values: List<List<Float>>, maxValue: Float?): StackFrame {
            val clean = values.map { series -> series.map(::sanitize) }
            val tallest = StackedBarGeometry.totals(clean).maxOrNull() ?: 0f
            return StackFrame(values = clean, max = maxValue?.let(::sanitize) ?: tallest)
        }

        private fun sanitize(value: Float): Float = if (value.isNaN()) 0f else value.coerceAtLeast(0f)
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

@Preview(showBackground = true)
@Composable
private fun StackedBarChartPreview() {
    StackedBarChart(
        series = listOf(
            BarSeries("Store", listOf(70f, 40f, 22f, 40f, 17f, 21f, 13f, 8f), StackedBarChartDefaults.BottomColor),
            BarSeries("Web", listOf(30f, 62f, 55f, 28f, 15f, 19f, 10f, 6f), StackedBarChartDefaults.MiddleColor),
            BarSeries("App", listOf(30f, 18f, 29f, 15f, 30f, 10f, 17f, 40f), StackedBarChartDefaults.TopColor),
        ),
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp)
            .fillMaxWidth()
            .height(StackedBarChartDefaults.ChartHeight),
    )
}
