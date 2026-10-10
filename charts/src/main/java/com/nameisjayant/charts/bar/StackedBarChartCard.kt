package com.nameisjayant.charts.bar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.charts.common.ChartBarsIcon
import com.nameisjayant.charts.common.ChartCard
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartLegend
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.charts.common.ChartLegendStyle
import com.nameisjayant.charts.common.ChartSparkIcon
import com.nameisjayant.charts.common.LegendItem

/** Look of a [StackedBarChartCard]'s trend label. Build one with [StackedBarChartCardDefaults.style]. */
@Immutable
public data class StackedBarChartCardStyle(
    val trendTextStyle: TextStyle,
    val trendIconColor: Color,
    val trendIconSpacing: Dp,
)

public object StackedBarChartCardDefaults {
    public val TrendIconColor: Color = Color(0xFF4499C8)

    public val TrendTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
    )

    public val SubtitleTextStyle: TextStyle = TextStyle(
        color = Color(0xFF4A4A4A),
        fontSize = 17.sp,
        lineHeight = 26.sp,
    )

    public fun style(
        trendTextStyle: TextStyle = TrendTextStyle,
        trendIconColor: Color = TrendIconColor,
        trendIconSpacing: Dp = 8.dp,
    ): StackedBarChartCardStyle = StackedBarChartCardStyle(
        trendTextStyle = trendTextStyle,
        trendIconColor = trendIconColor,
        trendIconSpacing = trendIconSpacing,
    )

    /** [ChartCardDefaults.style] with a larger, darker description set a little further below the title. */
    public fun cardStyle(
        subtitleTextStyle: TextStyle = SubtitleTextStyle,
        titleSubtitleSpacing: Dp = 12.dp,
        headerLegendSpacing: Dp = 32.dp,
        legendContentSpacing: Dp = 24.dp,
    ): ChartCardStyle = ChartCardDefaults.style(
        subtitleTextStyle = subtitleTextStyle,
        titleSubtitleSpacing = titleSubtitleSpacing,
        headerLegendSpacing = headerLegendSpacing,
        legendContentSpacing = legendContentSpacing,
    )
}

/**
 * A [StackedBarChart] framed in a [ChartCard]: a leading [icon] and [title], an optional [trend]
 * label (e.g. `+86%`) behind a small zigzag in the top-right corner, an optional [subtitle], then
 * the stacks.
 *
 * The legend, built from the series' labels and colours, is off by default; turn it on with
 * [showLegend]. [action] replaces the trend label with your own content.
 */
@Composable
public fun StackedBarChartCard(
    title: String,
    series: List<BarSeries>,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trend: String? = null,
    icon: (@Composable () -> Unit)? = { ChartBarsIcon(barHeights = listOf(0.45f, 0.7f, 1f)) },
    action: (@Composable () -> Unit)? = null,
    showLegend: Boolean = false,
    chartHeight: Dp = StackedBarChartDefaults.ChartHeight,
    maxValue: Float? = null,
    cardStyle: ChartCardStyle = StackedBarChartCardDefaults.cardStyle(),
    legendStyle: ChartLegendStyle = ChartLegendDefaults.style(),
    chartStyle: StackedBarChartStyle = StackedBarChartDefaults.style(),
    contentStyle: StackedBarChartCardStyle = StackedBarChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = StackedBarChartDefaults.AnimationSpec,
) {
    val legendItems = remember(series) { series.map { LegendItem(label = it.label, color = it.color) } }
    ChartCard(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        icon = icon,
        action = action ?: trend?.let { { StackedBarTrend(text = it, style = contentStyle) } },
        legend = if (showLegend) {
            { ChartLegend(items = legendItems, style = legendStyle) }
        } else {
            null
        },
        style = cardStyle,
    ) {
        StackedBarChart(
            series = series,
            style = chartStyle,
            maxValue = maxValue,
            animationSpec = animationSpec,
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
        )
    }
}

@Composable
private fun StackedBarTrend(text: String, style: StackedBarChartCardStyle) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ChartSparkIcon(color = style.trendIconColor)
        Spacer(Modifier.width(style.trendIconSpacing))
        BasicText(text = text, style = style.trendTextStyle)
    }
}

@Preview
@Composable
private fun StackedBarChartCardPreview() {
    StackedBarChartCard(
        title = "Bar chart",
        subtitle = "Orders per day, split by channel: store, website and mobile app.",
        trend = "+86%",
        series = listOf(
            BarSeries("Store", listOf(70f, 40f, 22f, 40f, 17f, 21f, 13f, 8f), StackedBarChartDefaults.BottomColor),
            BarSeries("Web", listOf(30f, 62f, 55f, 28f, 15f, 19f, 10f, 6f), StackedBarChartDefaults.MiddleColor),
            BarSeries("App", listOf(30f, 18f, 29f, 15f, 30f, 10f, 17f, 40f), StackedBarChartDefaults.TopColor),
        ),
        animationSpec = null,
    )
}
