package com.nameisjayant.composecharts.feature.stackedcolumn

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.StackedColumnSampleData

data class StackedColumnState(
    val month: Int = StackedColumnSampleData.DEFAULT_MONTH,
    val apples: List<Float> = StackedColumnSampleData.apples[StackedColumnSampleData.DEFAULT_MONTH],
    val bananas: List<Float> = StackedColumnSampleData.bananas[StackedColumnSampleData.DEFAULT_MONTH],
) : UiState

sealed interface StackedColumnIntent : UiIntent {
    data class SelectMonth(val index: Int) : StackedColumnIntent
    data object Shuffle : StackedColumnIntent
    data object Reset : StackedColumnIntent
    data object OpenChat : StackedColumnIntent
}

sealed interface StackedColumnEffect : UiEffect {
    data class ShowChat(val name: String) : StackedColumnEffect
}
