package com.nameisjayant.charts.donut

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.charts.common.ChartCard
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartDoughnutIcon
import com.nameisjayant.charts.common.ChartLegend
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.charts.common.ChartLegendStyle
import com.nameisjayant.charts.common.LegendItem
import com.nameisjayant.charts.common.chartCardSurface

/** Layout of a [DoughnutChartCard]'s content. Build one with [DoughnutChartCardDefaults.style]. */
@Immutable
public data class DoughnutChartCardStyle(
    /** Fill behind the title and subtitle, set apart from the card's background. */
    val headerBackgroundColor: Color,
    val legendChartSpacing: Dp,
    val chartFooterSpacing: Dp,
)

public object DoughnutChartCardDefaults {
    public val HeaderBackgroundColor: Color = Color(0xFFFAFAFA)

    public val SubtitleTextStyle: TextStyle = TextStyle(
        color = Color(0xFF424242),
        fontSize = 17.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.25.sp,
    )

    public fun style(
        headerBackgroundColor: Color = HeaderBackgroundColor,
        legendChartSpacing: Dp = 32.dp,
        chartFooterSpacing: Dp = 40.dp,
    ): DoughnutChartCardStyle = DoughnutChartCardStyle(
        headerBackgroundColor = headerBackgroundColor,
        legendChartSpacing = legendChartSpacing,
        chartFooterSpacing = chartFooterSpacing,
    )

    /** [ChartCardDefaults.style] with a darker, roomier subtitle sitting well below the title. */
    public fun cardStyle(
        subtitleTextStyle: TextStyle = SubtitleTextStyle,
        titleSubtitleSpacing: Dp = 16.dp,
    ): ChartCardStyle = ChartCardDefaults.style(
        subtitleTextStyle = subtitleTextStyle,
        titleSubtitleSpacing = titleSubtitleSpacing,
        iconTitleSpacing = 12.dp,
    )
}

/**
 * A [DoughnutChart] in a card: a tinted header holding [title], an optional leading [icon] and
 * [subtitle], then the legend, the chart, and an optional [footer] (e.g. who the data belongs to).
 *
 * Unlike [ChartCard], the header has its own background reaching the card's edges, so
 * [ChartCardStyle.contentPadding] pads the header and the body separately.
 */
@Composable
public fun DoughnutChartCard(
    title: String,
    segments: List<DonutSegment>,
    modifier: Modifier = Modifier,
    maxValue: Float? = null,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = { ChartDoughnutIcon() },
    footer: (@Composable () -> Unit)? = null,
    showLegend: Boolean = true,
    chartSize: Dp = DoughnutChartDefaults.ChartSize,
    centerLabelFormatter: (Float) -> String = DonutChartDefaults.PercentFormatter,
    cardStyle: ChartCardStyle = DoughnutChartCardDefaults.cardStyle(),
    legendStyle: ChartLegendStyle = ChartLegendDefaults.style(),
    chartStyle: DoughnutChartStyle = DoughnutChartDefaults.style(),
    contentStyle: DoughnutChartCardStyle = DoughnutChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = DoughnutChartDefaults.AnimationSpec,
) {
    val legendItems = remember(segments) { segments.map { LegendItem(label = it.label, color = it.color) } }
    Column(modifier = modifier.chartCardSurface(cardStyle)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(contentStyle.headerBackgroundColor)
                .padding(cardStyle.contentPadding),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    icon()
                    Spacer(Modifier.width(cardStyle.iconTitleSpacing))
                }
                BasicText(text = title, style = cardStyle.titleTextStyle)
            }
            if (subtitle != null) {
                Spacer(Modifier.height(cardStyle.titleSubtitleSpacing))
                BasicText(text = subtitle, style = cardStyle.subtitleTextStyle)
            }
        }
        Column(modifier = Modifier.fillMaxWidth().padding(cardStyle.contentPadding)) {
            if (showLegend) {
                ChartLegend(items = legendItems, style = legendStyle)
                Spacer(Modifier.height(contentStyle.legendChartSpacing))
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                DoughnutChart(
                    segments = segments,
                    maxValue = maxValue,
                    style = chartStyle,
                    centerLabelFormatter = centerLabelFormatter,
                    animationSpec = animationSpec,
                    modifier = Modifier
                        .widthIn(max = chartSize)
                        .fillMaxWidth()
                        .aspectRatio(1f),
                )
            }
            if (footer != null) {
                Spacer(Modifier.height(contentStyle.chartFooterSpacing))
                footer()
            }
        }
    }
}

@Preview
@Composable
private fun DoughnutChartCardPreview() {
    DoughnutChartCard(
        title = "Doughnut",
        subtitle = "Browser market shares at a specific website, 2014",
        segments = listOf(
            DonutSegment("css", 11f, DoughnutChartDefaults.CssColor),
            DonutSegment("html", 12f, DoughnutChartDefaults.HtmlColor),
            DonutSegment("sass", 50f, DoughnutChartDefaults.SassColor),
        ),
        maxValue = 100f,
        footer = {
            BasicText(
                text = "Robert Fox",
                modifier = Modifier
                    .background(Color(0xFFF3F3F3), RoundedCornerShape(8.dp))
                    .padding(12.dp),
            )
        },
        animationSpec = null,
    )
}
