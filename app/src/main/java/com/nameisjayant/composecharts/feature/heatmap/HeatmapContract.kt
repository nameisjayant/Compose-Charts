package com.nameisjayant.composecharts.feature.heatmap

import com.nameisjayant.charts.core.mvi.UiEffect
import com.nameisjayant.charts.core.mvi.UiIntent
import com.nameisjayant.charts.core.mvi.UiState
import com.nameisjayant.composecharts.data.HeatmapSampleData

data class HeatmapState(
    val year: Int = HeatmapSampleData.DEFAULT_YEAR,
    val values: List<List<Float>> = HeatmapSampleData.values[HeatmapSampleData.DEFAULT_YEAR],
) : UiState {
    val total: Int get() = values.sumOf { row -> row.sumOf { it.toDouble() } }.toInt()
}

sealed interface HeatmapIntent : UiIntent {
    data class SelectYear(val index: Int) : HeatmapIntent
    data object Shuffle : HeatmapIntent
    data object Reset : HeatmapIntent
    data object OpenSettings : HeatmapIntent
    data object ShowMoreActivity : HeatmapIntent
}

sealed interface HeatmapEffect : UiEffect {
    data object ShowSettings : HeatmapEffect
    data object ShowActivity : HeatmapEffect
}
