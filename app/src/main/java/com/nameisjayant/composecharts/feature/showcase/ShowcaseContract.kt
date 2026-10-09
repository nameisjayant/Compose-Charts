package com.nameisjayant.composecharts.feature.showcase

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.ChartSample

data class ShowcaseState(
    val isLoading: Boolean = true,
    val samples: List<ChartSample> = emptyList(),
) : UiState

sealed interface ShowcaseIntent : UiIntent {
    data object Load : ShowcaseIntent
    data class OpenSample(val id: String) : ShowcaseIntent
}

sealed interface ShowcaseEffect : UiEffect {
    data class NavigateToSample(val id: String) : ShowcaseEffect
}
