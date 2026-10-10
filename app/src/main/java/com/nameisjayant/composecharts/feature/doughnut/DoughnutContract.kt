package com.nameisjayant.composecharts.feature.doughnut

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.DoughnutSampleData

data class DoughnutState(
    val shares: List<Float> = DoughnutSampleData.shares,
) : UiState

sealed interface DoughnutIntent : UiIntent {
    data object Shuffle : DoughnutIntent
    data object Reset : DoughnutIntent
}

sealed interface DoughnutEffect : UiEffect
