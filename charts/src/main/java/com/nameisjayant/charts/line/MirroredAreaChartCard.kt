package com.nameisjayant.charts.line

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartOverflowIcon
import com.nameisjayant.charts.common.chartCardSurface

/** Layout of a [MirroredAreaChartCard]'s content. Build one with [MirroredAreaChartCardDefaults.style]. */
@Immutable
public data class MirroredAreaChartCardStyle(
    val descriptionTextStyle: TextStyle,
    val summaryLabelTextStyle: TextStyle,
    val summaryValueTextStyle: TextStyle,
    /** Diameter of each swatch in the footer's overlapping row. */
    val swatchSize: Dp,
    /** Distance between the centres of neighbouring swatches; less than [swatchSize] overlaps them. */
    val swatchStep: Dp,
    /** Ring around each swatch that separates it from the one behind; match the card's background. */
    val swatchBorderColor: Color,
    val swatchBorderWidth: Dp,
    val titleDescriptionSpacing: Dp,
    val descriptionChartSpacing: Dp,
    val chartFooterSpacing: Dp,
    val labelSwatchSpacing: Dp,
    val swatchValueSpacing: Dp,
)

public object MirroredAreaChartCardDefaults {
    public val DescriptionTextStyle: TextStyle = TextStyle(
        color = Color(0xFF4A4A4A),
        fontSize = 17.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.25.sp,
    )

    public val SummaryLabelTextStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 15.sp)

    public val SummaryValueTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
    )

    public fun style(
        descriptionTextStyle: TextStyle = DescriptionTextStyle,
        summaryLabelTextStyle: TextStyle = SummaryLabelTextStyle,
        summaryValueTextStyle: TextStyle = SummaryValueTextStyle,
        swatchSize: Dp = 22.dp,
        swatchStep: Dp = 14.dp,
        swatchBorderColor: Color = Color.White,
        swatchBorderWidth: Dp = 1.5.dp,
        titleDescriptionSpacing: Dp = 12.dp,
        descriptionChartSpacing: Dp = 16.dp,
        chartFooterSpacing: Dp = 16.dp,
        labelSwatchSpacing: Dp = 6.dp,
        swatchValueSpacing: Dp = 24.dp,
    ): MirroredAreaChartCardStyle = MirroredAreaChartCardStyle(
        descriptionTextStyle = descriptionTextStyle,
        summaryLabelTextStyle = summaryLabelTextStyle,
        summaryValueTextStyle = summaryValueTextStyle,
        swatchSize = swatchSize,
        swatchStep = swatchStep,
        swatchBorderColor = swatchBorderColor,
        swatchBorderWidth = swatchBorderWidth,
        titleDescriptionSpacing = titleDescriptionSpacing,
        descriptionChartSpacing = descriptionChartSpacing,
        chartFooterSpacing = chartFooterSpacing,
        labelSwatchSpacing = labelSwatchSpacing,
        swatchValueSpacing = swatchValueSpacing,
    )

    /** [ChartCardDefaults.style] with a larger title. */
    public fun cardStyle(
        titleTextStyle: TextStyle = ChartCardDefaults.TitleTextStyle.copy(fontSize = 32.sp, lineHeight = 38.sp),
    ): ChartCardStyle = ChartCardDefaults.style(titleTextStyle = titleTextStyle)
}

/**
 * A [MirroredAreaChart] in a card: [title] with a [subtitle] (e.g. a date) on the right, an
 * optional [description], the chart running to the card's edges, then a footer holding a
 * [summaryLabel], a row of overlapping [swatches], a [summaryValue] and an [action].
 *
 * The description is left out when `null`, and the footer when [summaryLabel] and [summaryValue]
 * are `null`, [swatches] is empty and there is no [action].
 */
@Composable
public fun MirroredAreaChartCard(
    title: String,
    upperSeries: List<AreaSeries>,
    lowerSeries: List<AreaSeries>,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    description: String? = null,
    summaryLabel: String? = null,
    summaryValue: String? = null,
    swatches: List<Color> = MirroredAreaChartDefaults.ZoneColors,
    action: (@Composable () -> Unit)? = { ChartOverflowIcon() },
    maxValue: Float? = null,
    chartHeight: Dp = MirroredAreaChartDefaults.ChartHeight,
    cardStyle: ChartCardStyle = MirroredAreaChartCardDefaults.cardStyle(),
    chartStyle: MirroredAreaChartStyle = MirroredAreaChartDefaults.style(),
    contentStyle: MirroredAreaChartCardStyle = MirroredAreaChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = MirroredAreaChartDefaults.AnimationSpec,
) {
    Column(modifier = modifier.chartCardSurface(cardStyle)) {
        Column(modifier = Modifier.fillMaxWidth().padding(cardStyle.contentPadding)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                BasicText(
                    text = title,
                    style = cardStyle.titleTextStyle,
                    modifier = Modifier.weight(1f).alignByBaseline(),
                )
                if (subtitle != null) {
                    BasicText(
                        text = subtitle,
                        style = cardStyle.subtitleTextStyle,
                        modifier = Modifier.alignByBaseline(),
                    )
                }
            }
            if (description != null) {
                Spacer(Modifier.height(contentStyle.titleDescriptionSpacing))
                BasicText(text = description, style = contentStyle.descriptionTextStyle)
            }
        }
        Spacer(Modifier.height(contentStyle.descriptionChartSpacing))
        MirroredAreaChart(
            upperSeries = upperSeries,
            lowerSeries = lowerSeries,
            maxValue = maxValue,
            style = chartStyle,
            animationSpec = animationSpec,
            modifier = Modifier.fillMaxWidth().height(chartHeight),
        )
        Spacer(Modifier.height(contentStyle.chartFooterSpacing))
        if (summaryLabel != null || summaryValue != null || swatches.isNotEmpty() || action != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(cardStyle.contentPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (summaryLabel != null) {
                        BasicText(text = summaryLabel, style = contentStyle.summaryLabelTextStyle)
                        Spacer(Modifier.height(contentStyle.labelSwatchSpacing))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (swatches.isNotEmpty()) {
                            SwatchStack(colors = swatches, style = contentStyle)
                            if (summaryValue != null) Spacer(Modifier.width(contentStyle.swatchValueSpacing))
                        }
                        if (summaryValue != null) {
                            BasicText(text = summaryValue, style = contentStyle.summaryValueTextStyle)
                        }
                    }
                }
                action?.invoke()
            }
        }
    }
}

/** [colors] as circles in a row, each overlapping the one before it. */
@Composable
private fun SwatchStack(colors: List<Color>, style: MirroredAreaChartCardStyle) {
    val width = style.swatchSize + style.swatchStep * (colors.size - 1)
    Canvas(modifier = Modifier.size(DpSize(width = width, height = style.swatchSize))) {
        val radius = style.swatchSize.toPx() / 2
        val step = style.swatchStep.toPx()
        val border = style.swatchBorderWidth.toPx()
        colors.forEachIndexed { index, color ->
            val center = Offset(radius + step * index, radius)
            if (index > 0 && border > 0f) drawCircle(color = style.swatchBorderColor, radius = radius, center = center)
            drawCircle(color = color, radius = if (index > 0) radius - border else radius, center = center)
        }
    }
}

@Preview
@Composable
private fun MirroredAreaChartCardPreview() {
    MirroredAreaChartCard(
        title = "Intensity",
        subtitle = "September 2021",
        description = "Minim dolor in amet nulla laboris enim dolore consequatt.",
        upperSeries = listOf(
            listOf(60f, 30f, 75f, 85f, 40f, 100f, 45f, 70f, 98f, 85f, 40f, 65f, 100f, 55f, 70f, 35f, 45f, 60f),
            listOf(45f, 20f, 50f, 70f, 30f, 55f, 40f, 80f, 75f, 82f, 35f, 50f, 75f, 60f, 45f, 40f, 30f, 50f),
            listOf(30f, 15f, 40f, 45f, 25f, 35f, 30f, 60f, 55f, 65f, 25f, 40f, 45f, 50f, 35f, 30f, 25f, 40f),
            listOf(12f, 8f, 20f, 25f, 15f, 12f, 10f, 30f, 32f, 28f, 15f, 25f, 22f, 18f, 20f, 15f, 10f, 12f),
        ).mapIndexed { index, values -> AreaSeries("", values, MirroredAreaChartDefaults.UpperColors[index]) },
        lowerSeries = listOf(
            listOf(60f, 45f, 70f, 50f, 95f, 40f, 65f, 55f, 85f, 90f, 45f, 40f, 95f, 50f, 40f, 55f, 50f, 40f),
            listOf(50f, 70f, 40f, 60f, 50f, 70f, 45f, 75f, 70f, 60f, 50f, 65f, 50f, 60f, 35f, 55f, 60f, 50f),
            listOf(30f, 45f, 22f, 50f, 28f, 55f, 20f, 40f, 52f, 30f, 48f, 25f, 55f, 35f, 20f, 45f, 30f, 38f),
        ).mapIndexed { index, values -> AreaSeries("", values, MirroredAreaChartDefaults.LowerColors[index]) },
        summaryLabel = "Intensity zones",
        summaryValue = "80%",
        animationSpec = null,
    )
}
