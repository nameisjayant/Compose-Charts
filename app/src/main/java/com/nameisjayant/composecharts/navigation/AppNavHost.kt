package com.nameisjayant.composecharts.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nameisjayant.composecharts.data.ChartSample
import com.nameisjayant.composecharts.feature.arearange.AreaRangeLineRoute
import com.nameisjayant.composecharts.feature.donut.DonutRoute
import com.nameisjayant.composecharts.feature.doughnut.DoughnutRoute
import com.nameisjayant.composecharts.feature.horizontalbar.HorizontalBarRoute
import com.nameisjayant.composecharts.feature.rankedbar.RankedBarRoute
import com.nameisjayant.composecharts.feature.showcase.ShowcaseRoute
import com.nameisjayant.composecharts.feature.splinearea.SplineAreaRoute
import com.nameisjayant.composecharts.feature.stackedbar.StackedBarRoute
import com.nameisjayant.composecharts.feature.verticalbar.VerticalBarRoute

/** Showcase list plus one level of sample screens; the open sample survives config changes. */
@Composable
fun AppNavHost() {
    var openSampleId by rememberSaveable { mutableStateOf<String?>(null) }
    val closeSample = { openSampleId = null }

    BackHandler(enabled = openSampleId != null, onBack = closeSample)

    when (openSampleId) {
        ChartSample.VERTICAL_BAR -> VerticalBarRoute(onBack = closeSample)
        ChartSample.AREA_RANGE_LINE -> AreaRangeLineRoute(onBack = closeSample)
        ChartSample.DONUT -> DonutRoute(onBack = closeSample)
        ChartSample.DOUGHNUT -> DoughnutRoute(onBack = closeSample)
        ChartSample.SPLINE_AREA -> SplineAreaRoute(onBack = closeSample)
        ChartSample.STACKED_BAR -> StackedBarRoute(onBack = closeSample)
        ChartSample.HORIZONTAL_BAR -> HorizontalBarRoute(onBack = closeSample)
        ChartSample.RANKED_BAR -> RankedBarRoute(onBack = closeSample)
        else -> ShowcaseRoute(onOpenSample = { openSampleId = it })
    }
}
