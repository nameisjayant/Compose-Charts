package com.nameisjayant.charts.line

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nameisjayant.charts.common.ChartCard
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartFilterIcon
import com.nameisjayant.charts.common.ChartLegend
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.charts.common.ChartLegendStyle
import com.nameisjayant.charts.common.LegendItem
import kotlin.math.roundToInt

/**
 * An [AreaRangeLineChart] framed in a [ChartCard].
 *
 * Every piece is configurable: the card through [cardStyle], the optional legend through
 * [legendStyle] and the chart through [chartStyle]. [icon] leads the title (e.g. a thermometer for
 * temperatures) and [action] trails it (e.g. a menu built around [ChartFilterIcon]).
 */
@Composable
public fun AreaRangeLineChartCard(
    title: String,
    series: AreaRangeLineSeries,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    showLegend: Boolean = false,
    chartHeight: Dp = AreaRangeLineChartDefaults.ChartHeight,
    xRange: AxisRange? = null,
    yRange: AxisRange? = null,
    xLabelFormatter: (Float) -> String = AreaRangeLineChartDefaults.LabelFormatter,
    yLabelFormatter: (Float) -> String = AreaRangeLineChartDefaults.LabelFormatter,
    cardStyle: ChartCardStyle = AreaRangeLineChartCardDefaults.cardStyle(),
    legendStyle: ChartLegendStyle = ChartLegendDefaults.style(),
    chartStyle: AreaRangeLineChartStyle = AreaRangeLineChartDefaults.style(),
    animationSpec: AnimationSpec<Float>? = AreaRangeLineChartDefaults.AnimationSpec,
) {
    val legendItems = remember(series) { listOf(LegendItem(label = series.label, color = series.bandColor)) }
    ChartCard(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        icon = icon,
        action = action,
        legend = if (showLegend) {
            { ChartLegend(items = legendItems, style = legendStyle) }
        } else {
            null
        },
        style = cardStyle,
    ) {
        AreaRangeLineChart(
            series = series,
            style = chartStyle,
            xRange = xRange,
            yRange = yRange,
            xLabelFormatter = xLabelFormatter,
            yLabelFormatter = yLabelFormatter,
            animationSpec = animationSpec,
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
        )
    }
}

public object AreaRangeLineChartCardDefaults {
    /** [ChartCardDefaults.style] with the subtitle set apart from the title, as a caption for the chart. */
    public fun cardStyle(
        titleSubtitleSpacing: Dp = 28.dp,
        headerLegendSpacing: Dp = 24.dp,
    ): ChartCardStyle = ChartCardDefaults.style(
        titleSubtitleSpacing = titleSubtitleSpacing,
        headerLegendSpacing = headerLegendSpacing,
    )
}

@Preview
@Composable
private fun AreaRangeLineChartCardPreview() {
    AreaRangeLineChartCard(
        title = "Temperatures",
        subtitle = "Average temperatures for July",
        action = { ChartFilterIcon() },
        series = AreaRangeLineSeries(
            label = "Temperature",
            points = listOf(
                LinePoint(-77f, 75f),
                LinePoint(-39f, 53f),
                LinePoint(-66f, 24f),
                LinePoint(-40f, 14f),
                LinePoint(-17f, 5f),
            ),
            lineColor = AreaRangeLineChartDefaults.LineColor,
            bandColor = AreaRangeLineChartDefaults.BandColor,
            pointColor = AreaRangeLineChartDefaults.PointColor,
        ),
        xRange = AxisRange(min = -80f, max = -10f, step = 10f),
        yRange = AxisRange(min = 0f, max = 75f, step = 25f),
        yLabelFormatter = { "${it.roundToInt()} km" },
        animationSpec = null,
    )
}
