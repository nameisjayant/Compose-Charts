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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * One bar of a [FloatingBarChart], spanning from [low] up to [high] and drawn in [color].
 *
 * The two ends may be given in either order. Negative and NaN values are drawn as 0.
 */
@Immutable
public data class FloatingBar(
    val low: Float,
    val high: Float,
    val color: Color,
)

/** Visual configuration of a [FloatingBarChart]. Build one with [FloatingBarChartDefaults.style]. */
@Immutable
public data class FloatingBarChartStyle(
    /** Widest a bar may get; bars get narrower when they would otherwise touch. */
    val barWidth: Dp,
    val barCornerRadius: Dp,
    /** Gap between the grid's ends and the first and last bars. */
    val barInset: Dp,
    val showGridLines: Boolean,
    val gridLineColor: Color,
    val gridLineWidth: Dp,
    /** Length of each dash of a grid line; 0 draws solid lines. */
    val gridDashLength: Dp,
    val gridGapLength: Dp,
    val showAxisLabels: Boolean,
    val labelTextStyle: TextStyle,
    /** Gap between the axis labels and the grid. */
    val labelSpacing: Dp,
)

public object FloatingBarChartDefaults {
    public val PrimaryColor: Color = Color(0xFF3375B5)
    public val LightColor: Color = Color(0xFFBDE6F3)
    public val AccentColor: Color = Color(0xFF03045E)
    public val GridLineColor: Color = Color(0xFF000000)
    public val ChartHeight: Dp = 168.dp

    public val LabelTextStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 14.sp)

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 800, easing = FastOutSlowInEasing)

    /** [barCornerRadius] defaults to half of [barWidth], giving pill-shaped bars. */
    public fun style(
        barWidth: Dp = 16.dp,
        barCornerRadius: Dp = barWidth / 2,
        barInset: Dp = 12.dp,
        showGridLines: Boolean = true,
        gridLineColor: Color = GridLineColor,
        gridLineWidth: Dp = 1.dp,
        gridDashLength: Dp = 5.dp,
        gridGapLength: Dp = 5.dp,
        showAxisLabels: Boolean = true,
        labelTextStyle: TextStyle = LabelTextStyle,
        labelSpacing: Dp = 16.dp,
    ): FloatingBarChartStyle = FloatingBarChartStyle(
        barWidth = barWidth,
        barCornerRadius = barCornerRadius,
        barInset = barInset,
        showGridLines = showGridLines,
        gridLineColor = gridLineColor,
        gridLineWidth = gridLineWidth,
        gridDashLength = gridDashLength,
        gridGapLength = gridGapLength,
        showAxisLabels = showAxisLabels,
        labelTextStyle = labelTextStyle,
        labelSpacing = labelSpacing,
    )

    /** Whole thousands as `8k`, other thousands as `2.5k`, smaller values as plain numbers. */
    public fun formatValue(value: Float): String = when {
        value >= 1000f -> {
            val thousands = value / 1000f
            if (thousands == floor(thousands)) "${thousands.toInt()}k" else "${(thousands * 10).roundToInt() / 10f}k"
        }
        value == floor(value) -> value.toInt().toString()
        else -> value.toString()
    }
}

/**
 * A column chart of [bars] that float: each spans from its own low value to its own high value
 * instead of standing on the baseline, over dashed horizontal grid lines with value labels on
 * the left.
 *
 * The axis runs from 0 to [maxValue] in [gridSteps] equal steps. By default [maxValue] is the
 * highest value rounded up so each step is a round number (1, 2, 2.5 or 5 × 10ⁿ). Bars are
 * spread evenly across the grid.
 *
 * @param labelFormatter turns an axis value into its label, e.g. `8000f` into `8k`.
 * @param animationSpec how bars grow in from their middles and move between data changes; `null`
 *   disables animation.
 */
@Composable
public fun FloatingBarChart(
    bars: List<FloatingBar>,
    modifier: Modifier = Modifier,
    style: FloatingBarChartStyle = FloatingBarChartDefaults.style(),
    maxValue: Float? = null,
    gridSteps: Int = 4,
    labelFormatter: (Float) -> String = FloatingBarChartDefaults::formatValue,
    animationSpec: AnimationSpec<Float>? = FloatingBarChartDefaults.AnimationSpec,
) {
    val target = remember(bars.map { it.low to it.high }, maxValue, gridSteps) {
        FloatingFrame.of(bars, maxValue, gridSteps)
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

    // Labels only depend on the axis being animated to, so measure them once per change.
    val textMeasurer = rememberTextMeasurer()
    val labels = remember(to.max, to.steps, style.showAxisLabels, style.labelTextStyle, labelFormatter) {
        if (!style.showAxisLabels) {
            emptyList()
        } else {
            to.ticks().map { textMeasurer.measure(labelFormatter(it), style.labelTextStyle) }
        }
    }

    val colors = bars.map { it.color }
    Canvas(modifier = modifier) {
        drawFloatingBars(
            frame = from.lerpTo(to, progress.value),
            steps = to.steps,
            labels = labels,
            colors = colors,
            style = style,
        )
    }
}

private fun DrawScope.drawFloatingBars(
    frame: FloatingFrame,
    steps: Int,
    labels: List<TextLayoutResult>,
    colors: List<Color>,
    style: FloatingBarChartStyle,
) {
    // Half a label's height above the top line and below the bottom one keeps the end labels on canvas.
    val labelWidth = labels.maxOfOrNull { it.size.width } ?: 0
    val labelHalfHeight = (labels.maxOfOrNull { it.size.height } ?: 0) / 2f
    val gridHalfWidth = style.gridLineWidth.toPx() / 2
    val left = if (labels.isEmpty()) 0f else labelWidth + style.labelSpacing.toPx()
    val top = max(labelHalfHeight, gridHalfWidth)
    val bottom = size.height - top
    if (size.width - left <= 0f || bottom - top <= 0f || frame.max <= 0f) return

    fun screenY(value: Float): Float = bottom - value / frame.max * (bottom - top)

    if (style.showGridLines || labels.isNotEmpty()) {
        val dash = style.gridDashLength.toPx()
        val effect = if (dash > 0f) PathEffect.dashPathEffect(floatArrayOf(dash, style.gridGapLength.toPx())) else null
        for (step in 0..steps) {
            val y = bottom - (bottom - top) * step / steps
            if (style.showGridLines) {
                drawLine(
                    color = style.gridLineColor,
                    start = Offset(left, y),
                    end = Offset(size.width, y),
                    strokeWidth = style.gridLineWidth.toPx(),
                    pathEffect = effect,
                )
            }
            labels.getOrNull(step)?.let { drawText(it, topLeft = Offset(0f, y - it.size.height / 2f)) }
        }
    }

    val count = frame.bars.size
    if (count == 0) return
    val inset = style.barInset.toPx()
    val barWidth = FloatingBarGeometry.barWidth(count, size.width - left - inset * 2, style.barWidth.toPx())
    if (barWidth <= 0f) return
    val cornerRadius = min(style.barCornerRadius.toPx(), barWidth / 2)
    frame.bars.forEachIndexed { index, (low, high) ->
        val barTop = screenY(min(high, frame.max))
        val barBottom = screenY(min(low, frame.max))
        val height = barBottom - barTop
        if (height <= 0f) return@forEachIndexed
        drawRoundRect(
            color = colors.getOrElse(index) { Color.Transparent },
            topLeft = Offset(left + inset + FloatingBarGeometry.left(index, count, size.width - left - inset * 2, barWidth), barTop),
            size = Size(barWidth, height),
            cornerRadius = CornerRadius(min(cornerRadius, height / 2)),
        )
    }
}

/** The scale and placement of a [FloatingBarChart], kept apart from drawing so it can be tested. */
internal object FloatingBarGeometry {
    /** The smallest [steps] round steps (each 1, 2, 2.5 or 5 × 10ⁿ) that reach [highest]. */
    fun niceMax(highest: Float, steps: Int): Float {
        if (highest <= 0f || steps <= 0) return 0f
        val rawStep = highest / steps
        val magnitude = 10f.pow(floor(log10(rawStep)))
        val step = when (rawStep / magnitude) {
            in 0f..1f -> 1f
            in 1f..2f -> 2f
            in 2f..2.5f -> 2.5f
            in 2.5f..5f -> 5f
            else -> 10f
        } * magnitude
        return step * steps
    }

    /** [barWidth] unless [count] bars would then fill [width] with less than a bar's width between them. */
    fun barWidth(count: Int, width: Float, barWidth: Float): Float {
        if (count <= 0) return 0f
        return min(barWidth, width / (count * 2 - 1)).coerceAtLeast(0f)
    }

    /** Left edge of bar [index]: the first touches the left edge, the last the right; a lone bar is centred. */
    fun left(index: Int, count: Int, width: Float, barWidth: Float): Float =
        if (count <= 1) (width - barWidth) / 2 else (width - barWidth) * index / (count - 1)
}

/** The bar ends (`low` to `high`) and axis maximum shown at one end of an animation. */
private data class FloatingFrame(
    val bars: List<Pair<Float, Float>>,
    val max: Float,
    val steps: Int,
) {
    fun ticks(): List<Float> = List(steps + 1) { max * it / steps }

    /** Each bar shrunk to its middle on the same axis, the starting point of the entry animation. */
    fun collapsed(): FloatingFrame = copy(bars = bars.map { (low, high) -> ((low + high) / 2).let { it to it } })

    /** Takes on [target]'s shape; bars missing here grow from their middle. */
    fun lerpTo(target: FloatingFrame, fraction: Float): FloatingFrame = FloatingFrame(
        bars = target.bars.mapIndexed { index, (low, high) ->
            val (startLow, startHigh) = bars.getOrElse(index) { ((low + high) / 2).let { it to it } }
            lerp(startLow, low, fraction) to lerp(startHigh, high, fraction)
        },
        max = lerp(max, target.max, fraction),
        steps = target.steps,
    )

    companion object {
        fun of(bars: List<FloatingBar>, maxValue: Float?, steps: Int): FloatingFrame {
            val clean = bars.map { bar ->
                val a = sanitize(bar.low)
                val b = sanitize(bar.high)
                min(a, b) to max(a, b)
            }
            val safeSteps = steps.coerceAtLeast(1)
            val highest = clean.maxOfOrNull { it.second } ?: 0f
            return FloatingFrame(
                bars = clean,
                max = maxValue?.let(::sanitize) ?: FloatingBarGeometry.niceMax(highest, safeSteps),
                steps = safeSteps,
            )
        }

        private fun sanitize(value: Float): Float = if (value.isNaN()) 0f else value.coerceAtLeast(0f)
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

@Preview(showBackground = true)
@Composable
private fun FloatingBarChartPreview() {
    val primary = FloatingBarChartDefaults.PrimaryColor
    val light = FloatingBarChartDefaults.LightColor
    FloatingBarChart(
        bars = listOf(
            FloatingBar(2800f, 5000f, primary),
            FloatingBar(1800f, 3000f, light),
            FloatingBar(3200f, 6000f, primary),
            FloatingBar(3800f, 5100f, light),
            FloatingBar(500f, 5000f, FloatingBarChartDefaults.AccentColor),
            FloatingBar(2200f, 7800f, light),
            FloatingBar(1700f, 4800f, primary),
            FloatingBar(2100f, 5700f, light),
            FloatingBar(3100f, 4400f, primary),
        ),
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp)
            .fillMaxWidth()
            .height(FloatingBarChartDefaults.ChartHeight),
    )
}
