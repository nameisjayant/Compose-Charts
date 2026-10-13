package com.nameisjayant.composecharts.feature.floatingbar

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.FloatingBarSampleData

data class FloatingBarState(
    val range: Int = FloatingBarSampleData.DEFAULT_RANGE,
    val lows: List<Float> = FloatingBarSampleData.lows[FloatingBarSampleData.DEFAULT_RANGE],
    val highs: List<Float> = FloatingBarSampleData.highs[FloatingBarSampleData.DEFAULT_RANGE],
) : UiState

sealed interface FloatingBarIntent : UiIntent {
    data class SelectRange(val index: Int) : FloatingBarIntent
    data object Shuffle : FloatingBarIntent
    data object Reset : FloatingBarIntent
    data object OpenFilters : FloatingBarIntent
}

sealed interface FloatingBarEffect : UiEffect {
    data object ShowFilters : FloatingBarEffect
}
