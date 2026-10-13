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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/** Visual configuration of a [StackedColumnChart]. Build one with [StackedColumnChartDefaults.style]. */
@Immutable
public data class StackedColumnChartStyle(
    /** Gap between neighbouring columns. */
    val columnSpacing: Dp,
    /** Gap between the series inside one column; 0 lets them meet flush. */
    val seriesSpacing: Dp,
    /** Widest a column may get; columns otherwise share the width left after [columnSpacing]. */
    val maxColumnWidth: Dp,
)

public object StackedColumnChartDefaults {
    public val FirstColor: Color = Color(0xFF7ECCE4)
    public val SecondColor: Color = Color(0xFF03045E)
    public val ChartHeight: Dp = 136.dp

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 800, easing = FastOutSlowInEasing)

    public fun style(
        columnSpacing: Dp = 15.dp,
        seriesSpacing: Dp = 0.dp,
        maxColumnWidth: Dp = Dp.Infinity,
    ): StackedColumnChartStyle = StackedColumnChartStyle(
        columnSpacing = columnSpacing,
        seriesSpacing = seriesSpacing,
        maxColumnWidth = maxColumnWidth,
    )
}

/**
 * A column chart whose columns are split into one stripe per [series]: column `i` holds value `i`
 * of every series side by side, the first series on the left, each in its own colour and each as
 * tall as its own value.
 *
 * Columns stand on the bottom edge and are spread edge to edge, sharing the width left after
 * [StackedColumnChartStyle.columnSpacing]. There are no axes or labels.
 *
 * @param maxValue value that fills the chart's height; defaults to the tallest value. Taller
 *   values are clipped.
 * @param animationSpec how columns grow in and between data changes; `null` disables animation.
 */
@Composable
public fun StackedColumnChart(
    series: List<BarSeries>,
    modifier: Modifier = Modifier,
    style: StackedColumnChartStyle = StackedColumnChartDefaults.style(),
    maxValue: Float? = null,
    animationSpec: AnimationSpec<Float>? = StackedColumnChartDefaults.AnimationSpec,
) {
    val target = remember(series.map { it.values }, maxValue) { ColumnFrame.of(series.map { it.values }, maxValue) }
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
    Canvas(modifier = modifier) {
        drawColumns(frame = from.lerpTo(to, progress.value), colors = colors, style = style)
    }
}

private fun DrawScope.drawColumns(
    frame: ColumnFrame,
    colors: List<Color>,
    style: StackedColumnChartStyle,
) {
    val count = frame.count
    val seriesCount = frame.values.size
    if (count == 0 || seriesCount == 0 || frame.max <= 0f || size.width <= 0f || size.height <= 0f) return
    val pxPerUnit = size.height / frame.max
    val columnWidth = StackedColumnGeometry.columnWidth(
        count = count,
        width = size.width,
        spacing = style.columnSpacing.toPx(),
        maxColumnWidth = style.maxColumnWidth.toPx(),
    )
    val seriesSpacing = style.seriesSpacing.toPx()
    val stripeWidth = StackedColumnGeometry.stripeWidth(seriesCount, columnWidth, seriesSpacing)
    if (stripeWidth <= 0f) return

    for (index in 0 until count) {
        val left = StackedColumnGeometry.left(index, count, size.width, columnWidth)
        frame.values.forEachIndexed { seriesIndex, values ->
            val height = min(values.getOrElse(index) { 0f } * pxPerUnit, size.height)
            if (height <= 0f) return@forEachIndexed
            drawRect(
                color = colors.getOrElse(seriesIndex) { Color.Transparent },
                topLeft = Offset(left + seriesIndex * (stripeWidth + seriesSpacing), size.height - height),
                size = Size(stripeWidth, height),
            )
        }
    }
}

/** Where the columns of a [StackedColumnChart] sit, kept apart from drawing so it can be tested. */
internal object StackedColumnGeometry {
    /** Width of each of [count] columns sharing [width] with [spacing] between them, capped at [maxColumnWidth]. */
    fun columnWidth(count: Int, width: Float, spacing: Float, maxColumnWidth: Float): Float {
        if (count <= 0) return 0f
        val shared = (width - spacing * (count - 1)) / count
        return min(shared, maxColumnWidth).coerceAtLeast(0f)
    }

    /** Width of each of [seriesCount] stripes sharing a column of [columnWidth] with [spacing] between them. */
    fun stripeWidth(seriesCount: Int, columnWidth: Float, spacing: Float): Float {
        if (seriesCount <= 0) return 0f
        return ((columnWidth - spacing * (seriesCount - 1)) / seriesCount).coerceAtLeast(0f)
    }

    /** Left edge of column [index]: the first touches the left edge, the last the right; a lone column is centred. */
    fun left(index: Int, count: Int, width: Float, columnWidth: Float): Float =
        if (count <= 1) (width - columnWidth) / 2 else (width - columnWidth) * index / (count - 1)
}

/** The values (`values[series][column]`) and scale shown at one end of an animation. */
private data class ColumnFrame(
    val values: List<List<Float>>,
    val max: Float,
) {
    val count: Int = values.maxOfOrNull { it.size } ?: 0

    /** Same scale with every column at zero height, the starting point of the entry animation. */
    fun collapsed(): ColumnFrame = copy(values = values.map { series -> series.map { 0f } })

    /** Takes on [target]'s shape; values missing on either side grow from or shrink to zero. */
    fun lerpTo(target: ColumnFrame, fraction: Float): ColumnFrame = ColumnFrame(
        values = List(target.values.size) { seriesIndex ->
            val start = values.getOrElse(seriesIndex) { emptyList() }
            val stop = target.values[seriesIndex]
            List(stop.size) { lerp(start.getOrElse(it) { 0f }, stop[it], fraction) }
        },
        max = lerp(max, target.max, fraction),
    )

    companion object {
        fun of(values: List<List<Float>>, maxValue: Float?): ColumnFrame {
            val clean = values.map { series -> series.map(::sanitize) }
            val tallest = clean.maxOfOrNull { series -> series.maxOrNull() ?: 0f } ?: 0f
            return ColumnFrame(values = clean, max = maxValue?.let(::sanitize) ?: tallest)
        }

        private fun sanitize(value: Float): Float = if (value.isNaN()) 0f else value.coerceAtLeast(0f)
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

@Preview(showBackground = true)
@Composable
private fun StackedColumnChartPreview() {
    StackedColumnChart(
        series = listOf(
            BarSeries("Apple", listOf(100f, 73f, 55f, 85f, 27f), StackedColumnChartDefaults.FirstColor),
            BarSeries("Banana", listOf(100f, 73f, 55f, 85f, 27f), StackedColumnChartDefaults.SecondColor),
        ),
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp)
            .fillMaxWidth()
            .height(StackedColumnChartDefaults.ChartHeight),
    )
}
