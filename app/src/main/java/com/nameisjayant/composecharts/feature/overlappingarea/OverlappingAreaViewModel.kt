package com.nameisjayant.composecharts.feature.overlappingarea

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.OverlappingAreaSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class OverlappingAreaViewModel @Inject constructor() :
    MviViewModel<OverlappingAreaState, OverlappingAreaIntent, OverlappingAreaEffect>(OverlappingAreaState()) {

    override fun handleIntent(intent: OverlappingAreaIntent) {
        when (intent) {
            is OverlappingAreaIntent.SelectRegion -> {
                val region = intent.index.coerceIn(OverlappingAreaSampleData.regions.indices)
                setState {
                    OverlappingAreaState(
                        region = region,
                        first = OverlappingAreaSampleData.first[region],
                        second = OverlappingAreaSampleData.second[region],
                    )
                }
            }
            OverlappingAreaIntent.Shuffle -> setState {
                copy(
                    first = OverlappingAreaSampleData.randomValues(Random.Default),
                    second = OverlappingAreaSampleData.randomValues(Random.Default),
                )
            }
            OverlappingAreaIntent.Reset -> setState { OverlappingAreaState() }
            OverlappingAreaIntent.OpenChat -> sendEffect(OverlappingAreaEffect.ShowChat(OverlappingAreaSampleData.NAME))
        }
    }
}
