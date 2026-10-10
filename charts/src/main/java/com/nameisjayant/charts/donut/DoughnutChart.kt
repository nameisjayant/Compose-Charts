package com.nameisjayant.charts.donut

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/** Visual configuration of a [DoughnutChart]. Build one with [DoughnutChartDefaults.style]. */
@Immutable
public data class DoughnutChartStyle(
    /** Radius of the hole in the middle, as a fraction of the chart's radius. */
    val holeRadiusFraction: Float,
    /** Outer radius of the first segment as a fraction of the chart's radius; the last one reaches the edge. */
    val firstSegmentRadiusFraction: Float,
    val showTrack: Boolean,
    val trackColor: Color,
    /** Outer radius of the track as a fraction of the chart's radius. */
    val trackRadiusFraction: Float,
    /** Where the last segment ends, in degrees clockwise from 3 o'clock. Segments run clockwise up to it. */
    val endAngle: Float,
    val cornerRadius: Dp,
    val holeColor: Color,
    val holeShadowColor: Color,
    val holeShadowWidth: Dp,
    val showCenterLabel: Boolean,
    val centerLabelTextStyle: TextStyle,
)

public object DoughnutChartDefaults {
    public val CssColor: Color = Color(0xFF07075C)
    public val HtmlColor: Color = Color(0xFF3375B5)
    public val SassColor: Color = Color(0xFF52B2D6)
    public val TrackColor: Color = Color(0xFFF5F5F5)
    public val HoleShadowColor: Color = Color(0x1F000000)
    public val ChartSize: Dp = 240.dp

    public val CenterLabelTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 32.sp,
    )

    public val AnimationSpec: AnimationSpec<Float> = DonutChartDefaults.AnimationSpec

    public fun style(
        holeRadiusFraction: Float = 0.57f,
        firstSegmentRadiusFraction: Float = 0.88f,
        showTrack: Boolean = true,
        trackColor: Color = TrackColor,
        trackRadiusFraction: Float = 0.79f,
        endAngle: Float = -90f,
        cornerRadius: Dp = 4.dp,
        holeColor: Color = Color.White,
        holeShadowColor: Color = HoleShadowColor,
        holeShadowWidth: Dp = 8.dp,
        showCenterLabel: Boolean = true,
        centerLabelTextStyle: TextStyle = CenterLabelTextStyle,
    ): DoughnutChartStyle = DoughnutChartStyle(
        holeRadiusFraction = holeRadiusFraction,
        firstSegmentRadiusFraction = firstSegmentRadiusFraction,
        showTrack = showTrack,
        trackColor = trackColor,
        trackRadiusFraction = trackRadiusFraction,
        endAngle = endAngle,
        cornerRadius = cornerRadius,
        holeColor = holeColor,
        holeShadowColor = holeShadowColor,
        holeShadowWidth = holeShadowWidth,
        showCenterLabel = showCenterLabel,
        centerLabelTextStyle = centerLabelTextStyle,
    )
}

/**
 * A ring of [segments] laid end to end around a hole, each one thicker than the one before, over
 * a thinner track, with the filled share written in the middle.
 *
 * The segments run clockwise and finish at [DoughnutChartStyle.endAngle] (12 o'clock by default),
 * so the unfilled track sits just before it. Segments that would run past a full ring are cut off.
 *
 * @param maxValue value of a full ring; defaults to the sum of the segments.
 * @param centerLabelFormatter turns the filled share (0..1) into the center label.
 * @param animationSpec how segments sweep in and between data changes; `null` disables animation.
 */
@Composable
public fun DoughnutChart(
    segments: List<DonutSegment>,
    modifier: Modifier = Modifier,
    maxValue: Float? = null,
    style: DoughnutChartStyle = DoughnutChartDefaults.style(),
    centerLabelFormatter: (Float) -> String = DonutChartDefaults.PercentFormatter,
    animationSpec: AnimationSpec<Float>? = DoughnutChartDefaults.AnimationSpec,
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
        drawDoughnut(frame = frame, colors = colors, style = style)
        if (style.showCenterLabel) {
            drawCenterLabel(currentFormatter(frame.filledFraction), style.centerLabelTextStyle, textMeasurer)
        }
    }
}

private fun DrawScope.drawDoughnut(frame: DonutFrame, colors: List<Color>, style: DoughnutChartStyle) {
    val radius = size.minDimension / 2
    if (radius <= 0f) return
    val holeRadius = radius * style.holeRadiusFraction.coerceIn(0.05f, 0.95f)

    if (style.showTrack) {
        val trackRadius = radius * style.trackRadiusFraction.coerceIn(0f, 1f)
        val trackWidth = trackRadius - holeRadius
        if (trackWidth > 0f) {
            drawCircle(
                color = style.trackColor,
                radius = holeRadius + trackWidth / 2,
                style = Stroke(width = trackWidth),
            )
        }
    }

    val sweeps = frame.sweeps()
    val starts = DoughnutGeometry.startAngles(sweeps, style.endAngle)
    val outerFractions = DoughnutGeometry.outerRadiusFractions(sweeps.size, style.firstSegmentRadiusFraction)
    val path = Path()
    for (index in sweeps.indices) {
        if (sweeps[index] <= 0f) continue
        path.rewind()
        path.addRoundedAnnularSector(
            center = center,
            innerRadius = holeRadius,
            outerRadius = (radius * outerFractions[index]).coerceAtLeast(holeRadius),
            startAngle = starts[index],
            sweepAngle = sweeps[index],
            cornerRadius = style.cornerRadius.toPx(),
        )
        drawPath(path = path, color = colors.getOrElse(index) { Color.Transparent })
    }

    // A soft shadow cast outward from the hole's rim, then the hole itself on top.
    val shadowWidth = style.holeShadowWidth.toPx()
    if (shadowWidth > 0f && style.holeShadowColor.alpha > 0f) {
        val shadowRadius = holeRadius + shadowWidth
        drawCircle(
            brush = Brush.radialGradient(
                (holeRadius / shadowRadius) to style.holeShadowColor,
                1f to Color.Transparent,
                center = center,
                radius = shadowRadius,
            ),
            radius = shadowRadius,
        )
    }
    drawCircle(color = style.holeColor, radius = holeRadius)
}

/**
 * Adds a ring slice from [startAngle] sweeping [sweepAngle] degrees clockwise, with its four
 * corners rounded by [cornerRadius] (shrunk to fit thin or short slices).
 */
private fun Path.addRoundedAnnularSector(
    center: Offset,
    innerRadius: Float,
    outerRadius: Float,
    startAngle: Float,
    sweepAngle: Float,
    cornerRadius: Float,
) {
    val sweepRadians = Math.toRadians(sweepAngle.toDouble()).toFloat()
    val corner = minOf(cornerRadius, (outerRadius - innerRadius) / 2, innerRadius * sweepRadians / 2)
        .coerceAtLeast(0f)
    val endAngle = startAngle + sweepAngle
    // How far, in degrees, a corner of that size reaches along each arc.
    val outerInset = Math.toDegrees((corner / outerRadius).toDouble()).toFloat()
    val innerInset = Math.toDegrees((corner / innerRadius).toDouble()).toFloat()

    fun point(r: Float, angle: Float): Offset {
        val radians = Math.toRadians(angle.toDouble())
        return Offset(center.x + r * cos(radians).toFloat(), center.y + r * sin(radians).toFloat())
    }
    fun quadTo(control: Offset, end: Offset) = quadraticTo(control.x, control.y, end.x, end.y)

    point(innerRadius + corner, startAngle).let { moveTo(it.x, it.y) }
    point(outerRadius - corner, startAngle).let { lineTo(it.x, it.y) }
    quadTo(point(outerRadius, startAngle), point(outerRadius, startAngle + outerInset))
    arcTo(Rect(center, outerRadius), startAngle + outerInset, sweepAngle - 2 * outerInset, false)
    quadTo(point(outerRadius, endAngle), point(outerRadius - corner, endAngle))
    point(innerRadius + corner, endAngle).let { lineTo(it.x, it.y) }
    quadTo(point(innerRadius, endAngle), point(innerRadius, endAngle - innerInset))
    arcTo(Rect(center, innerRadius), endAngle - innerInset, -(sweepAngle - 2 * innerInset), false)
    quadTo(point(innerRadius, startAngle), point(innerRadius + corner, startAngle))
    close()
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

/** Where each segment of a [DoughnutChart] sits, kept apart from drawing so it can be tested. */
internal object DoughnutGeometry {
    /** Start of each segment, laid clockwise so that the last one finishes at [endAngle]. */
    fun startAngles(sweeps: List<Float>, endAngle: Float): List<Float> {
        var angle = endAngle - sweeps.sum()
        return sweeps.map { sweep -> angle.also { angle += sweep } }
    }

    /** Outer radius of each of [count] segments, growing evenly from [first] to the full radius. */
    fun outerRadiusFractions(count: Int, first: Float): List<Float> {
        val start = first.coerceIn(0f, 1f)
        if (count <= 1) return List(count) { 1f }
        return List(count) { index -> start + (1f - start) * index / (count - 1) }
    }
}

@Preview(showBackground = true)
@Composable
private fun DoughnutChartPreview() {
    DoughnutChart(
        segments = listOf(
            DonutSegment("css", 11f, DoughnutChartDefaults.CssColor),
            DonutSegment("html", 12f, DoughnutChartDefaults.HtmlColor),
            DonutSegment("sass", 50f, DoughnutChartDefaults.SassColor),
        ),
        maxValue = 100f,
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp)
            .size(DoughnutChartDefaults.ChartSize),
    )
}
