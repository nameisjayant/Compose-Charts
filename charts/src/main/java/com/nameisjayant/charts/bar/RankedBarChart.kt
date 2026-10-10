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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

/**
 * One row of a [RankedBarChart]: a [label] written inside a bar as long as [value].
 *
 * Negative and NaN values are drawn as 0. Leave [color] `null` to take the palette colour for the
 * bar's rank.
 */
@Immutable
public data class RankedBar(
    val label: String,
    val value: Float,
    val color: Color? = null,
)

/** Visual configuration of a [RankedBarChart]. Build one with [RankedBarChartDefaults.style]. */
@Immutable
public data class RankedBarChartStyle(
    /** Colours by rank, darkest (longest bar) first; repeats when there are more bars. */
    val colors: List<Color>,
    /** Thickness of every bar; the bars touch, with no gap between them. */
    val barHeight: Dp,
    /** Rounds the outer corners of the staircase. */
    val cornerRadius: Dp,
    val labelTextStyle: TextStyle,
    /** Label colour on light bars. */
    val darkLabelColor: Color,
    /** Label colour on dark bars. */
    val lightLabelColor: Color,
    /** Colour of a label that doesn't fit in its bar and sits past its end, on the card background. */
    val outsideLabelColor: Color,
    /** Space between a bar's left edge and its label. */
    val labelPadding: Dp,
    val showLabels: Boolean,
)

public object RankedBarChartDefaults {
    /** Navy to pale sky, in eight steps. */
    public val Colors: List<Color> = listOf(
        Color(0xFF03045E),
        Color(0xFF1B3D86),
        Color(0xFF3375B1),
        Color(0xFF4495C5),
        Color(0xFF4FB0D6),
        Color(0xFF6EC6E2),
        Color(0xFF9EE8FB),
        Color(0xFFD2EEF6),
    )

    public val LabelTextStyle: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 800, easing = FastOutSlowInEasing)

    public fun style(
        colors: List<Color> = Colors,
        barHeight: Dp = 40.dp,
        cornerRadius: Dp = 6.dp,
        labelTextStyle: TextStyle = LabelTextStyle,
        darkLabelColor: Color = Color(0xFF000000),
        lightLabelColor: Color = Color.White,
        outsideLabelColor: Color = Color(0xFF000000),
        labelPadding: Dp = 16.dp,
        showLabels: Boolean = true,
    ): RankedBarChartStyle = RankedBarChartStyle(
        colors = colors,
        barHeight = barHeight,
        cornerRadius = cornerRadius,
        labelTextStyle = labelTextStyle,
        darkLabelColor = darkLabelColor,
        lightLabelColor = lightLabelColor,
        outsideLabelColor = outsideLabelColor,
        labelPadding = labelPadding,
        showLabels = showLabels,
    )
}

/**
 * Bars stacked flush on top of each other, longest first, so they form a staircase; each bar
 * carries its label and takes the next, lighter colour of [RankedBarChartStyle.colors].
 *
 * [bars] are sorted by value (ties keep their order). The chart sets its own height from the
 * number of bars and [RankedBarChartStyle.barHeight]; give it a width with [modifier]. A label is
 * written inside its bar, in white or black for contrast, and just past the bar's end when it
 * doesn't fit.
 *
 * @param maxValue length that fills the chart's width; defaults to the longest bar. Longer bars
 *   are clipped.
 * @param animationSpec how bars grow in and between data changes; `null` disables animation.
 */
@Composable
public fun RankedBarChart(
    bars: List<RankedBar>,
    modifier: Modifier = Modifier,
    style: RankedBarChartStyle = RankedBarChartDefaults.style(),
    maxValue: Float? = null,
    animationSpec: AnimationSpec<Float>? = RankedBarChartDefaults.AnimationSpec,
) {
    val ranked = remember(bars) { RankedBarGeometry.rank(bars) }
    val target = remember(ranked, maxValue) { RankedBarFrame.of(ranked, maxValue) }
    val currentAnimationSpec by rememberUpdatedState(animationSpec)
    val textMeasurer = rememberTextMeasurer()

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

    Canvas(modifier = modifier.height(style.barHeight * ranked.size)) {
        drawRankedBars(
            bars = ranked,
            frame = from.lerpTo(to, progress.value),
            target = to,
            style = style,
            textMeasurer = textMeasurer,
        )
    }
}

private fun DrawScope.drawRankedBars(
    bars: List<RankedBar>,
    frame: RankedBarFrame,
    target: RankedBarFrame,
    style: RankedBarChartStyle,
    textMeasurer: TextMeasurer,
) {
    if (frame.count == 0 || frame.max <= 0f || size.width <= 0f) return
    val pxPerUnit = size.width / frame.max
    val targetPxPerUnit = if (target.max > 0f) size.width / target.max else 0f
    val barHeight = style.barHeight.toPx()
    val labelPadding = style.labelPadding.toPx()

    for (index in 0 until frame.count) {
        val bar = bars.getOrNull(index) ?: continue
        val top = barHeight * index
        val length = min(frame.values[index] * pxPerUnit, size.width)
        val color = bar.color ?: style.colors.getOrElse(index % max(style.colors.size, 1)) { Color.Gray }
        if (length > 0f) {
            drawPath(
                path = RankedBarGeometry.barPath(index, frame.count, top, length, barHeight, style.cornerRadius.toPx()),
                color = color,
            )
        }
        if (!style.showLabels || bar.label.isEmpty()) continue

        val maxLabelWidth = (size.width - labelPadding * 2).toInt().coerceAtLeast(0)
        val layout = textMeasurer.measure(
            text = bar.label,
            style = style.labelTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            constraints = Constraints(maxWidth = maxLabelWidth),
        )
        val labelTop = top + (barHeight - layout.size.height) / 2
        // Decide inside or outside from where the bar ends up, so a label doesn't jump mid-animation.
        val finalLength = min(target.values.getOrElse(index) { 0f } * targetPxPerUnit, size.width)
        if (layout.size.width + labelPadding * 2 <= finalLength) {
            val labelColor = if (color.luminance() > 0.4f) style.darkLabelColor else style.lightLabelColor
            clipRect(left = 0f, top = top, right = length, bottom = top + barHeight) {
                drawText(layout, color = labelColor, topLeft = Offset(labelPadding, labelTop))
            }
        } else {
            val left = min(length + labelPadding / 2, size.width - layout.size.width)
            drawText(layout, color = style.outsideLabelColor, topLeft = Offset(max(left, 0f), labelTop))
        }
    }
}

/** Ordering and shapes of a [RankedBarChart], kept apart from drawing so they can be tested. */
internal object RankedBarGeometry {
    /** [bars] longest first; ties keep their order. */
    fun rank(bars: List<RankedBar>): List<RankedBar> = bars.sortedByDescending { sanitize(it.value) }

    /** The length that fills the width: [maxValue] if given, otherwise the longest bar. */
    fun scale(values: List<Float>, maxValue: Float?): Float =
        maxValue?.let(::sanitize) ?: (values.maxOrNull() ?: 0f)

    fun sanitize(value: Float): Float = if (value.isNaN()) 0f else value.coerceAtLeast(0f)

    /**
     * Which corners of bar [index] out of [count] are rounded, as top-left, top-right,
     * bottom-right, bottom-left: the top bar rounds its top corners, every other bar (or a lone bar) its
     * bottom-right corner (the step), and the last bar its bottom-left corner too.
     */
    fun roundedCorners(index: Int, count: Int): BooleanArray = booleanArrayOf(
        index == 0,
        index == 0,
        index != 0 || count == 1,
        index == count - 1,
    )

    fun barPath(index: Int, count: Int, top: Float, length: Float, height: Float, cornerRadius: Float): Path {
        val corners = roundedCorners(index, count)
        val radius = min(cornerRadius, min(length, height) / 2)
        fun corner(rounded: Boolean) = if (rounded) CornerRadius(radius) else CornerRadius.Zero
        return Path().apply {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = top,
                    right = length,
                    bottom = top + height,
                    topLeftCornerRadius = corner(corners[0]),
                    topRightCornerRadius = corner(corners[1]),
                    bottomRightCornerRadius = corner(corners[2]),
                    bottomLeftCornerRadius = corner(corners[3]),
                ),
            )
        }
    }
}

/** The bar lengths and scale shown at one end of an animation. */
private data class RankedBarFrame(
    val values: List<Float>,
    val max: Float,
) {
    val count: Int get() = values.size

    /** Same scale with every bar at zero length, the starting point of the entry animation. */
    fun collapsed(): RankedBarFrame = copy(values = values.map { 0f })

    /** Takes on [target]'s shape; bars missing on either side grow from zero. */
    fun lerpTo(target: RankedBarFrame, fraction: Float): RankedBarFrame = RankedBarFrame(
        values = List(target.count) { lerp(values.getOrElse(it) { 0f }, target.values[it], fraction) },
        max = lerp(max, target.max, fraction),
    )

    companion object {
        fun of(ranked: List<RankedBar>, maxValue: Float?): RankedBarFrame {
            val values = ranked.map { RankedBarGeometry.sanitize(it.value) }
            return RankedBarFrame(values, RankedBarGeometry.scale(values, maxValue))
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

@Preview(showBackground = true)
@Composable
private fun RankedBarChartPreview() {
    RankedBarChart(
        bars = listOf(
            RankedBar("Norway", 100f),
            RankedBar("Australia", 91f),
            RankedBar("Switzerland", 81f),
            RankedBar("Netherlands", 74f),
            RankedBar("United States", 63f),
            RankedBar("Germany", 61f),
            RankedBar("New Zealand", 46f),
            RankedBar("Canada", 37f),
        ),
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp)
            .fillMaxWidth(),
    )
}
