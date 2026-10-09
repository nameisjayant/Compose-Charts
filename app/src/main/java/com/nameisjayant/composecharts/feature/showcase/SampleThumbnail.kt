package com.nameisjayant.composecharts.feature.showcase

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nameisjayant.charts.bar.BarSeries
import com.nameisjayant.charts.bar.VerticalBarChart
import com.nameisjayant.charts.bar.VerticalBarChartDefaults
import com.nameisjayant.charts.line.AreaRangeLineChart
import com.nameisjayant.charts.line.AreaRangeLineChartDefaults
import com.nameisjayant.charts.line.AreaRangeLineSeries
import com.nameisjayant.composecharts.data.AreaRangeSampleData
import com.nameisjayant.composecharts.data.ChartSample
import com.nameisjayant.composecharts.data.VerticalBarSampleData

/** A live, miniature rendering of a sample's chart for its showcase tile. */
@Composable
fun SampleThumbnail(sampleId: String, modifier: Modifier = Modifier) {
    when (sampleId) {
        ChartSample.VERTICAL_BAR -> VerticalBarChart(
            upperSeries = BarSeries("", VerticalBarSampleData.upperValues, MaterialTheme.colorScheme.primary),
            lowerSeries = BarSeries("", VerticalBarSampleData.lowerValues, MaterialTheme.colorScheme.secondary),
            style = VerticalBarChartDefaults.style(
                barWidth = 9.dp,
                gridLineColor = MaterialTheme.colorScheme.outlineVariant,
                baselineColor = MaterialTheme.colorScheme.outlineVariant,
            ),
            modifier = modifier.fillMaxSize(),
        )
        ChartSample.AREA_RANGE_LINE -> AreaRangeLineChart(
            series = AreaRangeLineSeries(
                label = "",
                points = AreaRangeSampleData.points,
                lineColor = MaterialTheme.colorScheme.onSurface,
                bandColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f),
                pointColor = MaterialTheme.colorScheme.primary,
            ),
            xRange = AreaRangeSampleData.temperatureRange,
            yRange = AreaRangeSampleData.altitudeRange,
            style = AreaRangeLineChartDefaults.style(
                lineWidth = 1.5.dp,
                bandWidth = 10.dp,
                bandFadeLength = 16.dp,
                pointRadius = 3.dp,
                showAxisLabels = false,
                gridLineColor = MaterialTheme.colorScheme.outlineVariant,
            ),
            modifier = modifier.fillMaxSize(),
        )
    }
}
