package com.nameisjayant.composecharts.feature.horizontalbar

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.HorizontalBarSampleData

data class HorizontalBarState(
    val done: List<Float> = HorizontalBarSampleData.done,
    val assigned: List<Float> = HorizontalBarSampleData.assigned,
) : UiState

sealed interface HorizontalBarIntent : UiIntent {
    data object Shuffle : HorizontalBarIntent
    data object Reset : HorizontalBarIntent
    data object OpenProfile : HorizontalBarIntent
}

sealed interface HorizontalBarEffect : UiEffect {
    data class ShowProfile(val name: String) : HorizontalBarEffect
}
