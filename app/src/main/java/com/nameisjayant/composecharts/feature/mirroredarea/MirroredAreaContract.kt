package com.nameisjayant.composecharts.feature.mirroredarea

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.MirroredAreaSampleData

data class MirroredAreaState(
    val upper: List<List<Float>> = MirroredAreaSampleData.upper,
    val lower: List<List<Float>> = MirroredAreaSampleData.lower,
    val intensityPercent: Int = MirroredAreaSampleData.INTENSITY_PERCENT,
) : UiState

sealed interface MirroredAreaIntent : UiIntent {
    data object Shuffle : MirroredAreaIntent
    data object Reset : MirroredAreaIntent
}

sealed interface MirroredAreaEffect : UiEffect
