package com.nameisjayant.charts.bar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.nameisjayant.charts.common.ChartCard
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartLegend
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.charts.common.ChartLegendStyle
import com.nameisjayant.charts.common.ChartOverflowIcon
import com.nameisjayant.charts.common.LegendItem

/**
 * A [VerticalBarChart] framed in a [ChartCard], with a legend built from the series' labels
 * and colours.
 *
 * Every piece is configurable: the card through [cardStyle], the legend through [legendStyle] and
 * the bars through [chartStyle]. [action] is a trailing slot next to the title, e.g. an overflow
 * menu built around [ChartOverflowIcon].
 */
@Composable
public fun VerticalBarChartCard(
    title: String,
    upperSeries: BarSeries,
    modifier: Modifier = Modifier,
    lowerSeries: BarSeries? = null,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
    showLegend: Boolean = true,
    chartHeight: Dp = VerticalBarChartDefaults.ChartHeight,
    upperMaxValue: Float? = null,
    lowerMaxValue: Float? = null,
    cardStyle: ChartCardStyle = ChartCardDefaults.style(),
    legendStyle: ChartLegendStyle = ChartLegendDefaults.style(),
    chartStyle: VerticalBarChartStyle = VerticalBarChartDefaults.style(),
    animationSpec: AnimationSpec<Float>? = VerticalBarChartDefaults.AnimationSpec,
) {
    val legendItems = remember(upperSeries, lowerSeries) {
        listOfNotNull(upperSeries, lowerSeries).map { LegendItem(label = it.label, color = it.color) }
    }
    ChartCard(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        action = action,
        legend = if (showLegend) {
            { ChartLegend(items = legendItems, style = legendStyle) }
        } else {
            null
        },
        style = cardStyle,
    ) {
        VerticalBarChart(
            upperSeries = upperSeries,
            lowerSeries = lowerSeries,
            style = chartStyle,
            upperMaxValue = upperMaxValue,
            lowerMaxValue = lowerMaxValue,
            animationSpec = animationSpec,
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
        )
    }
}

@Preview
@Composable
private fun VerticalBarChartCardPreview() {
    VerticalBarChartCard(
        title = "Vertical bar",
        subtitle = "Statistics of the month",
        action = { ChartOverflowIcon() },
        upperSeries = BarSeries(
            label = "Data one",
            values = listOf(90f, 24f, 63f, 39f, 46f, 49f, 61f),
            color = VerticalBarChartDefaults.UpperBarColor,
        ),
        lowerSeries = BarSeries(
            label = "Data two",
            values = listOf(14f, 32f, 20f, 28f, 45f, 32f, 28f),
            color = VerticalBarChartDefaults.LowerBarColor,
        ),
        animationSpec = null,
    )
}
