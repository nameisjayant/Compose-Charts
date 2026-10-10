package com.nameisjayant.composecharts.feature.stackedbar

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.StackedBarSampleData

data class StackedBarState(
    val store: List<Float> = StackedBarSampleData.store,
    val web: List<Float> = StackedBarSampleData.web,
    val app: List<Float> = StackedBarSampleData.app,
    val changePercent: Int = StackedBarSampleData.CHANGE_PERCENT,
) : UiState

sealed interface StackedBarIntent : UiIntent {
    data object Shuffle : StackedBarIntent
    data object Reset : StackedBarIntent
}

sealed interface StackedBarEffect : UiEffect
