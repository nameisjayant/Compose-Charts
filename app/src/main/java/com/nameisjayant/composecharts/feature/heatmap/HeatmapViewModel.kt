package com.nameisjayant.composecharts.feature.heatmap

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.HeatmapSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HeatmapViewModel @Inject constructor() :
    MviViewModel<HeatmapState, HeatmapIntent, HeatmapEffect>(HeatmapState()) {

    override fun handleIntent(intent: HeatmapIntent) {
        when (intent) {
            is HeatmapIntent.SelectYear -> {
                val year = intent.index.coerceIn(HeatmapSampleData.years.indices)
                setState { HeatmapState(year = year, values = HeatmapSampleData.values[year]) }
            }
            HeatmapIntent.Shuffle -> setState { copy(values = HeatmapSampleData.randomValues(Random.Default)) }
            HeatmapIntent.Reset -> setState { HeatmapState() }
            HeatmapIntent.OpenSettings -> sendEffect(HeatmapEffect.ShowSettings)
            HeatmapIntent.ShowMoreActivity -> sendEffect(HeatmapEffect.ShowActivity)
        }
    }
}
