package com.nameisjayant.charts.donut

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * One slice of a [DonutChart]: [label] is what the legend shows, [value] sets the slice's length.
 * Negative and NaN values are drawn as 0.
 */
@Immutable
public data class DonutSegment(
    val label: String,
    val value: Float,
    val color: Color,
)

/** Visual configuration of a [DonutChart]. Build one with [DonutChartDefaults.style]. */
@Immutable
public data class DonutChartStyle(
    val strokeWidth: Dp,
    val showTrack: Boolean,
    val trackColor: Color,
    /** Where the first segment starts, in degrees clockwise from 3 o'clock. */
    val startAngle: Float,
    val roundedEnds: Boolean,
    val showCenterLabel: Boolean,
    val centerLabelTextStyle: TextStyle,
)

public object DonutChartDefaults {
    public val DocumentsColor: Color = Color(0xFF07075C)
    public val PhotosColor: Color = Color(0xFF3375B5)
    public val MusicColor: Color = Color(0xFFA4DEEC)
    public val VideosColor: Color = Color(0xFF52B2D6)
    public val TrackColor: Color = Color(0xFFF3F3F3)
    public val ChartSize: Dp = 160.dp

    public val CenterLabelTextStyle: TextStyle = TextStyle(
        color = Color(0xFFA3ABC3),
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
    )

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 900, easing = FastOutSlowInEasing)

    /** The filled share of the ring as a whole percentage, e.g. "77%". */
    public val PercentFormatter: (Float) -> String = { fraction -> "${(fraction * 100f).roundToInt()}%" }

    public fun style(
        strokeWidth: Dp = 24.dp,
        showTrack: Boolean = true,
        trackColor: Color = TrackColor,
        startAngle: Float = 0f,
        roundedEnds: Boolean = true,
        showCenterLabel: Boolean = true,
        centerLabelTextStyle: TextStyle = CenterLabelTextStyle,
    ): DonutChartStyle = DonutChartStyle(
        strokeWidth = strokeWidth,
        showTrack = showTrack,
        trackColor = trackColor,
        startAngle = startAngle,
        roundedEnds = roundedEnds,
        showCenterLabel = showCenterLabel,
        centerLabelTextStyle = centerLabelTextStyle,
    )
}

/**
 * A ring of [segments] laid end to end over a track, with the filled share written in the middle.
 *
 * With [maxValue] the ring is a gauge: e.g. storage used out of the device's capacity, so the
 * segments fill only part of it. Without it, the segments share the whole ring. Segments that
 * would run past a full ring are cut off. With rounded ends, each segment's end overlaps the
 * start of the next one.
 *
 * @param maxValue value of a full ring; defaults to the sum of the segments.
 * @param centerLabelFormatter turns the filled share (0..1) into the center label.
 * @param animationSpec how segments sweep in and between data changes; `null` disables animation.
 */
@Composable
public fun DonutChart(
    segments: List<DonutSegment>,
    modifier: Modifier = Modifier,
    maxValue: Float? = null,
    style: DonutChartStyle = DonutChartDefaults.style(),
    centerLabelFormatter: (Float) -> String = DonutChartDefaults.PercentFormatter,
    animationSpec: AnimationSpec<Float>? = DonutChartDefaults.AnimationSpec,
) {
    val target = remember(segments, maxValue) { DonutFrame.of(segments.map { it.value }, maxValue) }
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

    val colors = segments.map { it.color }
    val textMeasurer = rememberTextMeasurer()
    val currentFormatter by rememberUpdatedState(centerLabelFormatter)

    Canvas(modifier = modifier) {
        val frame = from.lerpTo(to, progress.value)
        drawDonut(frame = frame, colors = colors, style = style)
        if (style.showCenterLabel) {
            drawCenterLabel(currentFormatter(frame.filledFraction), style.centerLabelTextStyle, textMeasurer)
        }
    }
}

private fun DrawScope.drawDonut(frame: DonutFrame, colors: List<Color>, style: DonutChartStyle) {
    val strokeWidth = min(style.strokeWidth.toPx(), size.minDimension / 2)
    val diameter = size.minDimension - strokeWidth
    if (diameter <= 0f) return
    val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
    val arcSize = Size(diameter, diameter)

    if (style.showTrack) {
        drawArc(
            color = style.trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth),
        )
    }

    val sweeps = frame.sweeps()
    val total = sweeps.sum()
    if (total <= 0f) return

    // A rounded end pokes out past its arc by half the stroke. Shrink the chain so its two outer
    // ends stay inside the filled share; a full ring has no outer ends.
    val capAngle = if (style.roundedEnds) Math.toDegrees((strokeWidth / diameter).toDouble()).toFloat() else 0f
    val inset = if (total < FullCircle) min(capAngle, total / 2) else 0f
    val scale = (total - 2 * inset) / total
    val starts = FloatArray(sweeps.size)
    var angle = style.startAngle + inset
    for (index in sweeps.indices) {
        starts[index] = angle
        angle += sweeps[index] * scale
    }

    val stroke = Stroke(width = strokeWidth, cap = if (style.roundedEnds) StrokeCap.Round else StrokeCap.Butt)
    // Drawn last to first, so each segment's rounded end lies over the start of the next one.
    for (index in sweeps.indices.reversed()) {
        if (sweeps[index] <= 0f) continue
        drawArc(
            color = colors.getOrElse(index) { Color.Transparent },
            startAngle = starts[index],
            sweepAngle = sweeps[index] * scale,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke,
        )
    }
}

private fun DrawScope.drawCenterLabel(text: String, textStyle: TextStyle, textMeasurer: TextMeasurer) {
    val layout = textMeasurer.measure(text, textStyle)
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(
            x = (size.width - layout.size.width) / 2,
            y = (size.height - layout.size.height) / 2,
        ),
    )
}

/** The segment values and full-ring value shown at one end of an animation. */
internal data class DonutFrame(
    val values: List<Float>,
    val max: Float,
) {
    /** Share of the ring the segments fill, 0..1. */
    val filledFraction: Float get() = if (max > 0f) (values.sum() / max).coerceIn(0f, 1f) else 0f

    /** Sweep of each segment in degrees, cut off where the ring is full. */
    fun sweeps(): List<Float> {
        if (max <= 0f) return values.map { 0f }
        var remaining = FullCircle
        return values.map { value ->
            min(value / max * FullCircle, remaining).also { remaining -= it }
        }
    }

    /** Same full-ring value with every segment empty, the starting point of the entry animation. */
    fun collapsed(): DonutFrame = copy(values = values.map { 0f })

    fun lerpTo(target: DonutFrame, fraction: Float): DonutFrame = DonutFrame(
        values = List(target.values.size) { lerp(values.getOrElse(it) { 0f }, target.values[it], fraction) },
        max = lerp(max, target.max, fraction),
    )

    companion object {
        fun of(values: List<Float>, maxValue: Float?): DonutFrame {
            val clean = values.map(::sanitize)
            return DonutFrame(values = clean, max = maxValue?.let(::sanitize) ?: clean.sum())
        }

        private fun sanitize(value: Float): Float = if (value.isNaN()) 0f else value.coerceAtLeast(0f)
    }
}

private const val FullCircle = 360f

@Preview(showBackground = true)
@Composable
private fun DonutChartPreview() {
    DonutChart(
        segments = listOf(
            DonutSegment("Documents", 22f, DonutChartDefaults.DocumentsColor),
            DonutSegment("Photos", 47f, DonutChartDefaults.PhotosColor),
            DonutSegment("Music", 38f, DonutChartDefaults.MusicColor),
            DonutSegment("Videos", 30f, DonutChartDefaults.VideosColor),
        ),
        maxValue = 178f,
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp)
            .size(DonutChartDefaults.ChartSize),
    )
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction
