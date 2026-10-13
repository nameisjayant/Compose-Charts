package com.nameisjayant.composecharts.feature.overlappingarea

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.OverlappingAreaSampleData

data class OverlappingAreaState(
    val region: Int = 0,
    val first: List<Float> = OverlappingAreaSampleData.first[0],
    val second: List<Float> = OverlappingAreaSampleData.second[0],
) : UiState

sealed interface OverlappingAreaIntent : UiIntent {
    data class SelectRegion(val index: Int) : OverlappingAreaIntent
    data object Shuffle : OverlappingAreaIntent
    data object Reset : OverlappingAreaIntent
    data object OpenChat : OverlappingAreaIntent
}

sealed interface OverlappingAreaEffect : UiEffect {
    data class ShowChat(val name: String) : OverlappingAreaEffect
}
