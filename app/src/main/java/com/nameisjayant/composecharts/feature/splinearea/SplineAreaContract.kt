package com.nameisjayant.composecharts.feature.splinearea

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.SplineAreaSampleData

data class SplineAreaState(
    val earnings: Float = SplineAreaSampleData.EARNINGS,
    val changePercent: Float = SplineAreaSampleData.CHANGE_PERCENT,
    val trend: List<Float> = SplineAreaSampleData.trend,
) : UiState

sealed interface SplineAreaIntent : UiIntent {
    data object Shuffle : SplineAreaIntent
    data object Reset : SplineAreaIntent
}

sealed interface SplineAreaEffect : UiEffect
