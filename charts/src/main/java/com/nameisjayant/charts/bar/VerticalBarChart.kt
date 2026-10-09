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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/** Visual configuration of a [VerticalBarChart]. Build one with [VerticalBarChartDefaults.style]. */
@Immutable
public data class VerticalBarChartStyle(
    val barWidth: Dp,
    val barCornerRadius: Dp,
    val showGridLines: Boolean,
    val gridLineColor: Color,
    val gridLineWidth: Dp,
    val showBaseline: Boolean,
    val baselineColor: Color,
    val baselineWidth: Dp,
)

public object VerticalBarChartDefaults {
    public val UpperBarColor: Color = Color(0xFF3375B5)
    public val LowerBarColor: Color = Color(0xFF52B2D6)
    public val GridLineColor: Color = Color(0xFFECEDF4)
    public val ChartHeight: Dp = 160.dp

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 700, easing = FastOutSlowInEasing)

    /** [barCornerRadius] defaults to half of [barWidth], giving pill-shaped bar ends. */
    public fun style(
        barWidth: Dp = 20.dp,
        barCornerRadius: Dp = barWidth / 2,
        showGridLines: Boolean = true,
        gridLineColor: Color = GridLineColor,
        gridLineWidth: Dp = 1.dp,
        showBaseline: Boolean = true,
        baselineColor: Color = GridLineColor,
        baselineWidth: Dp = 1.dp,
    ): VerticalBarChartStyle = VerticalBarChartStyle(
        barWidth = barWidth,
        barCornerRadius = barCornerRadius,
        showGridLines = showGridLines,
        gridLineColor = gridLineColor,
        gridLineWidth = gridLineWidth,
        showBaseline = showBaseline,
        baselineColor = baselineColor,
        baselineWidth = baselineWidth,
    )
}

/**
 * A vertical bar chart whose bars grow up from a shared baseline and, when [lowerSeries] is given,
 * also down from it, so two series can be compared per category.
 *
 * Bars are spread edge to edge across the available width. The baseline sits where the tallest
 * upper bar and the deepest lower bar both fit, using one scale for both directions.
 *
 * @param upperSeries values drawn upward from the baseline.
 * @param lowerSeries values drawn downward from the baseline; `null` gives a plain bar chart.
 * @param upperMaxValue value that fills the space above the baseline; defaults to the largest
 *   upper value. Values above it are clipped.
 * @param lowerMaxValue the same for the space below the baseline.
 * @param animationSpec how bars animate in and between data changes; `null` disables animation.
 */
@Composable
public fun VerticalBarChart(
    upperSeries: BarSeries,
    modifier: Modifier = Modifier,
    lowerSeries: BarSeries? = null,
    style: VerticalBarChartStyle = VerticalBarChartDefaults.style(),
    upperMaxValue: Float? = null,
    lowerMaxValue: Float? = null,
    animationSpec: AnimationSpec<Float>? = VerticalBarChartDefaults.AnimationSpec,
) {
    val target = remember(upperSeries.values, lowerSeries?.values, upperMaxValue, lowerMaxValue) {
        BarFrame.of(
            upper = upperSeries.values,
            lower = lowerSeries?.values.orEmpty(),
            upperMax = upperMaxValue,
            lowerMax = lowerMaxValue,
        )
    }
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
    val upperColor = upperSeries.color
    val lowerColor = lowerSeries?.color ?: Color.Transparent
    val hasLower = lowerSeries != null

    Canvas(modifier = modifier) {
        drawBars(
            from = from,
            to = to,
            fraction = progress.value,
            hasLower = hasLower,
            upperColor = upperColor,
            lowerColor = lowerColor,
            style = style,
            path = path,
        )
    }
}

private fun DrawScope.drawBars(
    from: BarFrame,
    to: BarFrame,
    fraction: Float,
    hasLower: Boolean,
    upperColor: Color,
    lowerColor: Color,
    style: VerticalBarChartStyle,
    path: Path,
) {
    val upperMax = lerp(from.upperMax, to.upperMax, fraction)
    val lowerMax = if (hasLower) lerp(from.lowerMax, to.lowerMax, fraction) else 0f
    val totalMax = upperMax + lowerMax
    val pxPerUnit = if (totalMax > 0f) size.height / totalMax else 0f
    val baselineY = when {
        totalMax > 0f -> upperMax * pxPerUnit
        hasLower -> size.height / 2
        else -> size.height
    }

    val count = to.count
    val barWidth = if (count == 0) 0f else min(style.barWidth.toPx(), size.width / count)
    val step = if (count > 1) (size.width - barWidth) / (count - 1) else 0f
    fun centerX(index: Int): Float = if (count == 1) size.width / 2 else barWidth / 2 + step * index

    if (style.showGridLines) {
        for (index in 0 until count) {
            val x = centerX(index)
            drawLine(
                color = style.gridLineColor,
                start = Offset(x, 0f),
                end = Offset(x, baselineY),
                strokeWidth = style.gridLineWidth.toPx(),
            )
        }
    }
    if (style.showBaseline) {
        drawLine(
            color = style.baselineColor,
            start = Offset(0f, baselineY),
            end = Offset(size.width, baselineY),
            strokeWidth = style.baselineWidth.toPx(),
        )
    }

    val cornerRadius = style.barCornerRadius.toPx()
    for (index in 0 until count) {
        val left = centerX(index) - barWidth / 2
        val right = left + barWidth

        val upperHeight = min(lerp(from.upperAt(index), to.upperAt(index), fraction) * pxPerUnit, baselineY)
        if (upperHeight > 0f) {
            val radius = CornerRadius(min(cornerRadius, min(barWidth / 2, upperHeight)))
            path.rewind()
            path.addRoundRect(
                RoundRect(
                    left = left,
                    top = baselineY - upperHeight,
                    right = right,
                    bottom = baselineY,
                    topLeftCornerRadius = radius,
                    topRightCornerRadius = radius,
                    bottomRightCornerRadius = CornerRadius.Zero,
                    bottomLeftCornerRadius = CornerRadius.Zero,
                ),
            )
            drawPath(path, upperColor)
        }

        if (!hasLower) continue
        val lowerHeight = min(
            lerp(from.lowerAt(index), to.lowerAt(index), fraction) * pxPerUnit,
            size.height - baselineY,
        )
        if (lowerHeight > 0f) {
            val radius = CornerRadius(min(cornerRadius, min(barWidth / 2, lowerHeight)))
            path.rewind()
            path.addRoundRect(
                RoundRect(
                    left = left,
                    top = baselineY,
                    right = right,
                    bottom = baselineY + lowerHeight,
                    topLeftCornerRadius = CornerRadius.Zero,
                    topRightCornerRadius = CornerRadius.Zero,
                    bottomRightCornerRadius = radius,
                    bottomLeftCornerRadius = radius,
                ),
            )
            drawPath(path, lowerColor)
        }
    }
}

/** The bar values and scale shown at one end of an animation. */
private data class BarFrame(
    val upper: List<Float>,
    val lower: List<Float>,
    val upperMax: Float,
    val lowerMax: Float,
) {
    val count: Int get() = max(upper.size, lower.size)

    fun upperAt(index: Int): Float = upper.getOrElse(index) { 0f }

    fun lowerAt(index: Int): Float = lower.getOrElse(index) { 0f }

    /** Same scale with every bar at zero height, the starting point of the entry animation. */
    fun collapsed(): BarFrame = copy(upper = upper.map { 0f }, lower = lower.map { 0f })

    fun lerpTo(target: BarFrame, fraction: Float): BarFrame = BarFrame(
        upper = List(target.upper.size) { lerp(upperAt(it), target.upper[it], fraction) },
        lower = List(target.lower.size) { lerp(lowerAt(it), target.lower[it], fraction) },
        upperMax = lerp(upperMax, target.upperMax, fraction),
        lowerMax = lerp(lowerMax, target.lowerMax, fraction),
    )

    companion object {
        fun of(upper: List<Float>, lower: List<Float>, upperMax: Float?, lowerMax: Float?): BarFrame {
            val cleanUpper = upper.map(::sanitize)
            val cleanLower = lower.map(::sanitize)
            return BarFrame(
                upper = cleanUpper,
                lower = cleanLower,
                upperMax = upperMax?.let(::sanitize) ?: cleanUpper.maxOrNull() ?: 0f,
                lowerMax = lowerMax?.let(::sanitize) ?: cleanLower.maxOrNull() ?: 0f,
            )
        }

        private fun sanitize(value: Float): Float = if (value.isNaN()) 0f else value.coerceAtLeast(0f)
    }
}

@Preview(showBackground = true)
@Composable
private fun VerticalBarChartPreview() {
    VerticalBarChart(
        upperSeries = BarSeries(
            label = "Data one",
            values = listOf(90f, 24f, 63f, 39f, 46f, 49f, 61f),
            color = VerticalBarChartDefaults.UpperBarColor,
        ),
        lowerSeries = BarSeries(
            label = "Data two",
            values = listOf(14f, 32f, 20f, 28f, 45f, 32f, 28f),
            color = VerticalBarChartDefaults.LowerBarColor,
        ),
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp)
            .fillMaxWidth()
            .height(VerticalBarChartDefaults.ChartHeight),
    )
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction
