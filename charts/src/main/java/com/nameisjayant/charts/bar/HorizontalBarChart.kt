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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * One row of a [HorizontalBarChart]: a [value] drawn over a lighter track as long as [total],
 * e.g. tasks done out of tasks assigned.
 *
 * Negative and NaN numbers are drawn as 0. A [value] above [total] runs past the track.
 */
@Immutable
public data class HorizontalBar(
    val value: Float,
    val total: Float,
    val label: String = "",
)

/** Visual configuration of a [HorizontalBarChart]. Build one with [HorizontalBarChartDefaults.style]. */
@Immutable
public data class HorizontalBarChartStyle(
    val fillColor: Color,
    val trackColor: Color,
    /** Thickness of every bar. */
    val barHeight: Dp,
    /** Gap between neighbouring bars. */
    val barSpacing: Dp,
    /** Rounds both ends of the fill and the track; capped at half the bar, so the default draws pills. */
    val cornerRadius: Dp,
)

public object HorizontalBarChartDefaults {
    public val FillColor: Color = Color(0xFF3375B5)
    public val TrackColor: Color = Color(0xFFBAE5F3)

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 800, easing = FastOutSlowInEasing)

    public fun style(
        fillColor: Color = FillColor,
        trackColor: Color = TrackColor,
        barHeight: Dp = 24.dp,
        barSpacing: Dp = 8.dp,
        cornerRadius: Dp = Dp.Infinity,
    ): HorizontalBarChartStyle = HorizontalBarChartStyle(
        fillColor = fillColor,
        trackColor = trackColor,
        barHeight = barHeight,
        barSpacing = barSpacing,
        cornerRadius = cornerRadius,
    )
}

/**
 * Bars that grow from the left edge, one per entry of [bars], each a dark fill over a lighter
 * track (a progress bar per row).
 *
 * The chart sets its own height from the number of bars, [HorizontalBarChartStyle.barHeight] and
 * [HorizontalBarChartStyle.barSpacing]; give it a width with [modifier]. There are no axes or labels.
 *
 * @param maxValue length that fills the chart's width; defaults to the longest track or fill.
 *   Longer bars are clipped.
 * @param animationSpec how bars grow in and between data changes; `null` disables animation.
 */
@Composable
public fun HorizontalBarChart(
    bars: List<HorizontalBar>,
    modifier: Modifier = Modifier,
    style: HorizontalBarChartStyle = HorizontalBarChartDefaults.style(),
    maxValue: Float? = null,
    animationSpec: AnimationSpec<Float>? = HorizontalBarChartDefaults.AnimationSpec,
) {
    val target = remember(bars, maxValue) { HorizontalBarFrame.of(bars, maxValue) }
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

    Canvas(
        modifier = modifier.height(
            HorizontalBarGeometry.chartHeight(bars.size, style.barHeight, style.barSpacing),
        ),
    ) {
        drawBars(frame = from.lerpTo(to, progress.value), style = style)
    }
}

private fun DrawScope.drawBars(frame: HorizontalBarFrame, style: HorizontalBarChartStyle) {
    if (frame.count == 0 || frame.max <= 0f || size.width <= 0f) return
    val pxPerUnit = size.width / frame.max
    val barHeight = style.barHeight.toPx()
    val step = barHeight + style.barSpacing.toPx()
    val cornerRadius = style.cornerRadius.toPx()

    for (index in 0 until frame.count) {
        val top = step * index
        val track = min(frame.totals[index] * pxPerUnit, size.width)
        val fill = min(frame.values[index] * pxPerUnit, size.width)
        drawPill(style.trackColor, top, track, barHeight, cornerRadius)
        drawPill(style.fillColor, top, fill, barHeight, cornerRadius)
    }
}

private fun DrawScope.drawPill(color: Color, top: Float, length: Float, height: Float, cornerRadius: Float) {
    if (length <= 0f) return
    drawRoundRect(
        color = color,
        topLeft = Offset(0f, top),
        size = Size(length, height),
        cornerRadius = CornerRadius(min(cornerRadius, min(length, height) / 2)),
    )
}

/** Sizes of a [HorizontalBarChart], kept apart from drawing so they can be tested. */
internal object HorizontalBarGeometry {
    /** Height of [count] bars of [barHeight] with [spacing] between them. */
    fun chartHeight(count: Int, barHeight: Dp, spacing: Dp): Dp =
        if (count <= 0) 0.dp else barHeight * count + spacing * (count - 1)

    /** The length that fills the width: [maxValue] if given, otherwise the longest track or fill. */
    fun scale(values: List<Float>, totals: List<Float>, maxValue: Float?): Float =
        maxValue?.let(::sanitize) ?: max(values.maxOrNull() ?: 0f, totals.maxOrNull() ?: 0f)

    fun sanitize(value: Float): Float = if (value.isNaN()) 0f else value.coerceAtLeast(0f)
}

/** The fill and track lengths and scale shown at one end of an animation. */
private data class HorizontalBarFrame(
    val values: List<Float>,
    val totals: List<Float>,
    val max: Float,
) {
    val count: Int get() = values.size

    /** Same scale with every bar at zero length, the starting point of the entry animation. */
    fun collapsed(): HorizontalBarFrame = copy(values = values.map { 0f }, totals = totals.map { 0f })

    /** Takes on [target]'s shape; bars missing on either side grow from or shrink to zero. */
    fun lerpTo(target: HorizontalBarFrame, fraction: Float): HorizontalBarFrame = HorizontalBarFrame(
        values = List(target.count) { lerp(values.getOrElse(it) { 0f }, target.values[it], fraction) },
        totals = List(target.count) { lerp(totals.getOrElse(it) { 0f }, target.totals[it], fraction) },
        max = lerp(max, target.max, fraction),
    )

    companion object {
        fun of(bars: List<HorizontalBar>, maxValue: Float?): HorizontalBarFrame {
            val values = bars.map { HorizontalBarGeometry.sanitize(it.value) }
            val totals = bars.map { HorizontalBarGeometry.sanitize(it.total) }
            return HorizontalBarFrame(values, totals, HorizontalBarGeometry.scale(values, totals, maxValue))
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

@Preview(showBackground = true)
@Composable
private fun HorizontalBarChartPreview() {
    HorizontalBarChart(
        bars = listOf(
            HorizontalBar(31f, 60f),
            HorizontalBar(56f, 80f),
            HorizontalBar(30f, 51f),
            HorizontalBar(21f, 32f),
            HorizontalBar(38f, 63f),
        ),
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp)
            .fillMaxWidth(),
    )
}
