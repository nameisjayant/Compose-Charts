package com.nameisjayant.composecharts.feature.rankedbar

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.RankedBarSampleData

data class RankedBarState(
    val scores: List<Float> = RankedBarSampleData.scores,
) : UiState

sealed interface RankedBarIntent : UiIntent {
    data object Shuffle : RankedBarIntent
    data object Reset : RankedBarIntent
}

sealed interface RankedBarEffect : UiEffect
