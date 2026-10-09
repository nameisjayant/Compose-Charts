package com.nameisjayant.composecharts.feature.verticalbar

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.VerticalBarSampleData

data class VerticalBarState(
    val upperValues: List<Float> = VerticalBarSampleData.upperValues,
    val lowerValues: List<Float> = VerticalBarSampleData.lowerValues,
) : UiState

sealed interface VerticalBarIntent : UiIntent {
    data object Shuffle : VerticalBarIntent
    data object Reset : VerticalBarIntent
}

sealed interface VerticalBarEffect : UiEffect
