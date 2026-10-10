package com.nameisjayant.composecharts.feature.donut

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.DonutSampleData

data class DonutState(
    val usedGb: List<Float> = DonutSampleData.usedGb,
) : UiState {
    val totalUsedGb: Float get() = usedGb.sum()
}

sealed interface DonutIntent : UiIntent {
    data object Shuffle : DonutIntent
    data object Reset : DonutIntent
}

sealed interface DonutEffect : UiEffect
