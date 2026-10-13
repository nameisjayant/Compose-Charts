package com.nameisjayant.composecharts.feature.showcase

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nameisjayant.charts.bar.BarSeries
import com.nameisjayant.charts.bar.HorizontalBar
import com.nameisjayant.charts.bar.HorizontalBarChart
import com.nameisjayant.charts.bar.HorizontalBarChartDefaults
import com.nameisjayant.charts.bar.RankedBar
import com.nameisjayant.charts.bar.RankedBarChart
import com.nameisjayant.charts.bar.RankedBarChartDefaults
import com.nameisjayant.charts.bar.StackedBarChart
import com.nameisjayant.charts.bar.StackedBarChartDefaults
import com.nameisjayant.charts.bar.VerticalBarChart
import com.nameisjayant.charts.bar.VerticalBarChartDefaults
import com.nameisjayant.charts.donut.DonutChart
import com.nameisjayant.charts.donut.DonutChartDefaults
import com.nameisjayant.charts.donut.DonutSegment
import com.nameisjayant.charts.donut.DoughnutChart
import com.nameisjayant.charts.donut.DoughnutChartDefaults
import com.nameisjayant.charts.line.AreaRangeLineChart
import com.nameisjayant.charts.line.AreaRangeLineChartDefaults
import com.nameisjayant.charts.line.AreaRangeLineSeries
import com.nameisjayant.charts.line.AreaSeries
import com.nameisjayant.charts.line.MirroredAreaChart
import com.nameisjayant.charts.line.MirroredAreaChartDefaults
import com.nameisjayant.charts.line.OverlappingAreaChart
import com.nameisjayant.charts.line.OverlappingAreaChartDefaults
import com.nameisjayant.charts.line.SplineAreaChart
import com.nameisjayant.charts.line.SplineAreaChartDefaults
import com.nameisjayant.composecharts.data.AreaRangeSampleData
import com.nameisjayant.composecharts.data.ChartSample
import com.nameisjayant.composecharts.data.DonutSampleData
import com.nameisjayant.composecharts.data.DoughnutSampleData
import com.nameisjayant.composecharts.data.HorizontalBarSampleData
import com.nameisjayant.composecharts.data.MirroredAreaSampleData
import com.nameisjayant.composecharts.data.OverlappingAreaSampleData
import com.nameisjayant.composecharts.data.RankedBarSampleData
import com.nameisjayant.composecharts.data.SplineAreaSampleData
import com.nameisjayant.composecharts.data.StackedBarSampleData
import com.nameisjayant.composecharts.data.VerticalBarSampleData
import com.nameisjayant.composecharts.feature.donut.donutPalette
import com.nameisjayant.composecharts.feature.doughnut.doughnutPalette
import com.nameisjayant.composecharts.feature.stackedbar.stackedBarPalette

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
        ChartSample.DONUT -> {
            val palette = donutPalette()
            DonutChart(
                segments = DonutSampleData.usedGb.mapIndexed { index, value -> DonutSegment("", value, palette[index]) },
                maxValue = DonutSampleData.CAPACITY_GB,
                style = DonutChartDefaults.style(
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    showCenterLabel = false,
                ),
                modifier = modifier.fillMaxSize(),
            )
        }
        ChartSample.DOUGHNUT -> {
            val palette = doughnutPalette()
            DoughnutChart(
                segments = DoughnutSampleData.shares.mapIndexed { index, value -> DonutSegment("", value, palette[index]) },
                maxValue = DoughnutSampleData.TOTAL,
                style = DoughnutChartDefaults.style(
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    cornerRadius = 2.dp,
                    holeColor = MaterialTheme.colorScheme.surface,
                    holeShadowWidth = 3.dp,
                    showCenterLabel = false,
                ),
                modifier = modifier.fillMaxSize(),
            )
        }
        ChartSample.SPLINE_AREA -> SplineAreaChart(
            values = SplineAreaSampleData.trend,
            style = SplineAreaChartDefaults.style(lineWidth = 2.dp, bottomSpaceFraction = 0.3f),
            modifier = modifier.fillMaxSize(),
        )
        ChartSample.STACKED_BAR -> {
            val palette = stackedBarPalette()
            StackedBarChart(
                series = listOf(StackedBarSampleData.store, StackedBarSampleData.web, StackedBarSampleData.app)
                    .mapIndexed { index, values -> BarSeries("", values, palette[index]) },
                style = StackedBarChartDefaults.style(barSpacing = 3.dp, cornerRadius = 3.dp),
                modifier = modifier.fillMaxSize(),
            )
        }
        ChartSample.HORIZONTAL_BAR -> HorizontalBarChart(
            bars = HorizontalBarSampleData.done.zip(HorizontalBarSampleData.assigned, ::HorizontalBar),
            style = HorizontalBarChartDefaults.style(barHeight = 8.dp, barSpacing = 4.dp),
            modifier = modifier.fillMaxWidth(),
        )
        ChartSample.RANKED_BAR -> RankedBarChart(
            bars = RankedBarSampleData.scores.map { RankedBar(label = "", value = it) },
            style = RankedBarChartDefaults.style(barHeight = 7.dp, cornerRadius = 2.dp, showLabels = false),
            modifier = modifier.fillMaxWidth(),
        )
        ChartSample.OVERLAPPING_AREA -> OverlappingAreaChart(
            series = listOf(
                AreaSeries("", OverlappingAreaSampleData.first[0], OverlappingAreaChartDefaults.FirstColor),
                AreaSeries("", OverlappingAreaSampleData.second[0], OverlappingAreaChartDefaults.SecondColor),
            ),
            modifier = modifier.fillMaxSize(),
        )
        ChartSample.MIRRORED_AREA -> MirroredAreaChart(
            upperSeries = MirroredAreaSampleData.upper.mapIndexed { index, values ->
                AreaSeries("", values, MirroredAreaChartDefaults.UpperColors[index])
            },
            lowerSeries = MirroredAreaSampleData.lower.mapIndexed { index, values ->
                AreaSeries("", values, MirroredAreaChartDefaults.LowerColors[index])
            },
            modifier = modifier.fillMaxSize(),
        )
    }
}
