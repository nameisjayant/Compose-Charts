package com.nameisjayant.charts.donut

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.charts.common.ChartCard
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartLegend
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.charts.common.ChartLegendStyle
import com.nameisjayant.charts.common.ChartSettingsIcon
import com.nameisjayant.charts.common.LegendItem
import com.nameisjayant.charts.common.LegendOrientation
import com.nameisjayant.charts.common.chartCardFrame

/** Layout of a [DonutChartCard]'s content. Build one with [DonutChartCardDefaults.style]. */
@Immutable
public data class DonutChartCardStyle(
    val valueTextStyle: TextStyle,
    val swatchSize: Dp,
    /** How far each colour swatch in front of the value slides over the previous one. */
    val swatchOverlap: Dp,
    val swatchValueSpacing: Dp,
    val chartLegendSpacing: Dp,
    val chartTitleSpacing: Dp,
    val titleCaptionSpacing: Dp,
    val captionValueSpacing: Dp,
)

public object DonutChartCardDefaults {
    public val CaptionTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 17.sp,
        letterSpacing = 0.25.sp,
    )

    public val ValueTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
    )

    public fun style(
        valueTextStyle: TextStyle = ValueTextStyle,
        swatchSize: Dp = 24.dp,
        swatchOverlap: Dp = 12.dp,
        swatchValueSpacing: Dp = 12.dp,
        chartLegendSpacing: Dp = 32.dp,
        chartTitleSpacing: Dp = 28.dp,
        titleCaptionSpacing: Dp = 28.dp,
        captionValueSpacing: Dp = 6.dp,
    ): DonutChartCardStyle = DonutChartCardStyle(
        valueTextStyle = valueTextStyle,
        swatchSize = swatchSize,
        swatchOverlap = swatchOverlap,
        swatchValueSpacing = swatchValueSpacing,
        chartLegendSpacing = chartLegendSpacing,
        chartTitleSpacing = chartTitleSpacing,
        titleCaptionSpacing = titleCaptionSpacing,
        captionValueSpacing = captionValueSpacing,
    )

    /** [ChartCardDefaults.style] whose subtitle style, used for the caption, is dark like the title. */
    public fun cardStyle(
        captionTextStyle: TextStyle = CaptionTextStyle,
    ): ChartCardStyle = ChartCardDefaults.style(subtitleTextStyle = captionTextStyle)

    /** [ChartLegendDefaults.style] with square swatches stacked one item per line. */
    public fun legendStyle(
        textStyle: TextStyle = ChartLegendDefaults.TextStyle.copy(
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.25.sp,
        ),
    ): ChartLegendStyle = ChartLegendDefaults.style(
        textStyle = textStyle,
        indicatorSize = DpSize(16.dp, 16.dp),
        indicatorShape = RoundedCornerShape(4.dp),
        indicatorTextSpacing = 16.dp,
        lineSpacing = 12.dp,
    )
}

/**
 * A [DonutChart] with its legend beside it, then [title], and a summary row: [caption] over a stack
 * of the segment colours and [value], with [action] at the end (e.g. a settings button built
 * around [ChartSettingsIcon]).
 *
 * Unlike [ChartCard], the chart leads and the title sits below it, but it takes the same
 * [cardStyle]: background, shape, padding and title style. Its subtitle style is used for [caption].
 */
@Composable
public fun DonutChartCard(
    title: String,
    segments: List<DonutSegment>,
    modifier: Modifier = Modifier,
    maxValue: Float? = null,
    caption: String? = null,
    value: String? = null,
    action: (@Composable () -> Unit)? = null,
    showLegend: Boolean = true,
    showSwatches: Boolean = true,
    chartSize: Dp = DonutChartDefaults.ChartSize,
    centerLabelFormatter: (Float) -> String = DonutChartDefaults.PercentFormatter,
    cardStyle: ChartCardStyle = DonutChartCardDefaults.cardStyle(),
    legendStyle: ChartLegendStyle = DonutChartCardDefaults.legendStyle(),
    chartStyle: DonutChartStyle = DonutChartDefaults.style(),
    contentStyle: DonutChartCardStyle = DonutChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = DonutChartDefaults.AnimationSpec,
) {
    val legendItems = remember(segments) { segments.map { LegendItem(label = it.label, color = it.color) } }
    Column(modifier = modifier.chartCardFrame(cardStyle)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DonutChart(
                segments = segments,
                maxValue = maxValue,
                style = chartStyle,
                centerLabelFormatter = centerLabelFormatter,
                animationSpec = animationSpec,
                // Gives up width to the legend on narrow screens rather than squeezing it.
                modifier = Modifier
                    .weight(1f, fill = false)
                    .widthIn(max = chartSize)
                    .aspectRatio(1f),
            )
            if (showLegend) {
                Spacer(Modifier.width(contentStyle.chartLegendSpacing))
                ChartLegend(items = legendItems, style = legendStyle, orientation = LegendOrientation.Vertical)
            }
        }
        Spacer(Modifier.height(contentStyle.chartTitleSpacing))
        BasicText(text = title, style = cardStyle.titleTextStyle)

        if (caption == null && value == null && action == null) return@Column
        Spacer(Modifier.height(contentStyle.titleCaptionSpacing))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                if (caption != null) {
                    BasicText(text = caption, style = cardStyle.subtitleTextStyle)
                    Spacer(Modifier.height(contentStyle.captionValueSpacing))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showSwatches && segments.isNotEmpty()) {
                        ColorSwatchStack(
                            colors = segments.map { it.color },
                            swatchSize = contentStyle.swatchSize,
                            overlap = contentStyle.swatchOverlap,
                        )
                        Spacer(Modifier.width(contentStyle.swatchValueSpacing))
                    }
                    if (value != null) BasicText(text = value, style = contentStyle.valueTextStyle)
                }
            }
            action?.invoke()
        }
    }
}

/** Overlapping circles, one per colour, each later one in front of the one before. */
@Composable
private fun ColorSwatchStack(colors: List<Color>, swatchSize: Dp, overlap: Dp) {
    val step = (swatchSize - overlap).coerceAtLeast(0.dp)
    Canvas(modifier = Modifier.size(width = swatchSize + step * (colors.size - 1), height = swatchSize)) {
        val radius = swatchSize.toPx() / 2
        colors.forEachIndexed { index, color ->
            drawCircle(color = color, radius = radius, center = Offset(radius + step.toPx() * index, radius))
        }
    }
}

@Preview
@Composable
private fun DonutChartCardPreview() {
    DonutChartCard(
        title = "Storage of your device",
        segments = listOf(
            DonutSegment("Documents", 22f, DonutChartDefaults.DocumentsColor),
            DonutSegment("Photos", 47f, DonutChartDefaults.PhotosColor),
            DonutSegment("Music", 38f, DonutChartDefaults.MusicColor),
            DonutSegment("Videos", 30f, DonutChartDefaults.VideosColor),
        ),
        maxValue = 178f,
        caption = "Used storage",
        value = "137GB",
        action = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(0xFFF3F3F3), CircleShape),
                contentAlignment = Alignment.Center,
            ) { ChartSettingsIcon() }
        },
        animationSpec = null,
    )
}
