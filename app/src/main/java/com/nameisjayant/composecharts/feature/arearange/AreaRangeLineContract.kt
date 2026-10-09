package com.nameisjayant.composecharts.feature.arearange

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.charts.line.LinePoint
import com.nameisjayant.composecharts.data.AreaRangeSampleData

data class AreaRangeLineState(
    val points: List<LinePoint> = AreaRangeSampleData.points,
) : UiState

sealed interface AreaRangeLineIntent : UiIntent {
    data object Shuffle : AreaRangeLineIntent
    data object Reset : AreaRangeLineIntent
}

sealed interface AreaRangeLineEffect : UiEffect
