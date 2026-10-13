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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/** Visual configuration of a [MirroredAreaChart]. Build one with [MirroredAreaChartDefaults.style]. */
@Immutable
public data class MirroredAreaChartStyle(
    /** 0 joins values with straight lines; 1 gives a fully rounded curve. */
    val smoothing: Float,
    /** Share of the chart's height above the baseline; the rest is below it. */
    val upperFraction: Float,
    /** Colour of the line between the two halves; transparent hides it. */
    val baselineColor: Color,
    val baselineWidth: Dp,
)

public object MirroredAreaChartDefaults {
    /** Five shades from deep blue to pale sky, e.g. for intensity zones from hardest to easiest. */
    public val ZoneColors: List<Color> = listOf(
        Color(0xFF3375B5),
        Color(0xFF4499C8),
        Color(0xFF5AADD3),
        Color(0xFF86C3E0),
        Color(0xFFB5DCEB),
    )

    /** Colours for four series above the baseline, back to front: see-through, darkening towards the front. */
    public val UpperColors: List<Color> = listOf(
        ZoneColors[3].copy(alpha = 0.9f),
        ZoneColors[2].copy(alpha = 0.8f),
        ZoneColors[1].copy(alpha = 0.8f),
        ZoneColors[0],
    )

    /** Colours for three series below the baseline, back to front: see-through, darkening towards the front. */
    public val LowerColors: List<Color> = listOf(
        ZoneColors[4].copy(alpha = 0.8f),
        ZoneColors[3].copy(alpha = 0.8f),
        ZoneColors[2].copy(alpha = 0.9f),
    )

    public val ChartHeight: Dp = 240.dp

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 1000, easing = FastOutSlowInEasing)

    public fun style(
        smoothing: Float = 1f,
        upperFraction: Float = 0.5f,
        baselineColor: Color = Color(0x99FFFFFF),
        baselineWidth: Dp = 1.dp,
    ): MirroredAreaChartStyle = MirroredAreaChartStyle(
        smoothing = smoothing,
        upperFraction = upperFraction,
        baselineColor = baselineColor,
        baselineWidth = baselineWidth,
    )
}

/**
 * Smooth translucent areas rising above a shared baseline from [upperSeries] and hanging below
 * it from [lowerSeries], like a waveform. Each series spreads its values evenly from the left
 * edge to the right edge. There are no axes or labels.
 *
 * Within each half, series are drawn in order, so the last one is in front; put the tallest first
 * so the smaller ones stay visible. Both halves share one scale; give the chart a height with
 * [modifier].
 *
 * @param maxValue value drawn at the top (and bottom) edge; defaults to the largest value of any series.
 * @param animationSpec how the areas grow out of the baseline and morph between data changes;
 *   `null` disables animation.
 */
@Composable
public fun MirroredAreaChart(
    upperSeries: List<AreaSeries>,
    lowerSeries: List<AreaSeries>,
    modifier: Modifier = Modifier,
    maxValue: Float? = null,
    style: MirroredAreaChartStyle = MirroredAreaChartDefaults.style(),
    animationSpec: AnimationSpec<Float>? = MirroredAreaChartDefaults.AnimationSpec,
) {
    // Both halves animate as one frame, upper series first, so they share a scale.
    val series = remember(upperSeries, lowerSeries) { upperSeries + lowerSeries }
    val target = remember(series, maxValue) { AreaFrame.of(series, maxValue) }
    val currentAnimationSpec by rememberUpdatedState(animationSpec)

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
        drawMirroredAreas(
            series = series,
            upperCount = upperSeries.size,
            frame = from.lerpTo(to, progress.value),
            style = style,
            path = path,
        )
    }
}

private fun DrawScope.drawMirroredAreas(
    series: List<AreaSeries>,
    upperCount: Int,
    frame: AreaFrame,
    style: MirroredAreaChartStyle,
    path: Path,
) {
    if (size.width <= 0f || size.height <= 0f) return
    val baseline = size.height * style.upperFraction.coerceIn(0f, 1f)
    val smoothing = style.smoothing.coerceIn(0f, 1f)

    frame.values.forEachIndexed { index, values ->
        val color = series.getOrNull(index)?.color ?: return@forEachIndexed
        if (values.isEmpty()) return@forEachIndexed
        val upper = index < upperCount
        // Upper areas grow up from the baseline, lower ones down; a curve that overshoots the
        // baseline is clipped to its own half.
        val span = if (upper) -baseline else size.height - baseline
        // A lone value is drawn as a flat band across the whole width.
        val heights = if (values.size == 1) listOf(values[0], values[0]) else values
        val points = heights.mapIndexed { point, value ->
            Offset(
                x = OverlappingAreaGeometry.x(point, heights.size, size.width),
                y = baseline + OverlappingAreaGeometry.fraction(value, frame.max) * span,
            )
        }
        path.rewind()
        path.smoothThrough(points, smoothing)
        path.lineTo(size.width, baseline)
        path.lineTo(0f, baseline)
        path.close()
        clipRect(
            top = if (upper) 0f else baseline,
            bottom = if (upper) baseline else size.height,
        ) {
            drawPath(path, color)
        }
    }

    if (style.baselineColor.alpha > 0f) {
        val width = style.baselineWidth.toPx()
        drawRect(
            color = style.baselineColor,
            topLeft = Offset(0f, baseline - width / 2),
            size = Size(size.width, width),
        )
    }
}

/** Joins [points] with Catmull-Rom curves, rounded by [smoothing] from 0 (straight) to 1. */
private fun Path.smoothThrough(points: List<Offset>, smoothing: Float) {
    val first = points.first()
    moveTo(first.x, first.y)
    for (index in 0 until points.lastIndex) {
        val previous = points[max(index - 1, 0)]
        val start = points[index]
        val end = points[index + 1]
        val next = points[minOf(index + 2, points.lastIndex)]
        val control1 = start + (end - previous) * (smoothing / 6f)
        val control2 = end - (next - start) * (smoothing / 6f)
        cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
    }
}

@Preview(showBackground = true)
@Composable
private fun MirroredAreaChartPreview() {
    val upper = listOf(
        listOf(60f, 30f, 75f, 85f, 40f, 100f, 45f, 70f, 98f, 85f, 40f, 65f, 100f, 55f, 70f, 35f, 45f, 60f),
        listOf(45f, 20f, 50f, 70f, 30f, 55f, 40f, 80f, 75f, 82f, 35f, 50f, 75f, 60f, 45f, 40f, 30f, 50f),
        listOf(30f, 15f, 40f, 45f, 25f, 35f, 30f, 60f, 55f, 65f, 25f, 40f, 45f, 50f, 35f, 30f, 25f, 40f),
        listOf(12f, 8f, 20f, 25f, 15f, 12f, 10f, 30f, 32f, 28f, 15f, 25f, 22f, 18f, 20f, 15f, 10f, 12f),
    )
    val lower = listOf(
        listOf(60f, 45f, 70f, 50f, 95f, 40f, 65f, 55f, 85f, 90f, 45f, 40f, 95f, 50f, 40f, 55f, 50f, 40f),
        listOf(50f, 70f, 40f, 60f, 50f, 70f, 45f, 75f, 70f, 60f, 50f, 65f, 50f, 60f, 35f, 55f, 60f, 50f),
        listOf(30f, 45f, 22f, 50f, 28f, 55f, 20f, 40f, 52f, 30f, 48f, 25f, 55f, 35f, 20f, 45f, 30f, 38f),
    )
    MirroredAreaChart(
        upperSeries = upper.mapIndexed { index, values ->
            AreaSeries("", values, MirroredAreaChartDefaults.UpperColors[index])
        },
        lowerSeries = lower.mapIndexed { index, values ->
            AreaSeries("", values, MirroredAreaChartDefaults.LowerColors[index])
        },
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .height(MirroredAreaChartDefaults.ChartHeight),
    )
}
