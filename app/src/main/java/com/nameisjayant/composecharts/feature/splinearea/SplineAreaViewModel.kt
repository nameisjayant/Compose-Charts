package com.nameisjayant.composecharts.feature.splinearea

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.SplineAreaSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SplineAreaViewModel @Inject constructor() :
    MviViewModel<SplineAreaState, SplineAreaIntent, SplineAreaEffect>(SplineAreaState()) {

    override fun handleIntent(intent: SplineAreaIntent) {
        when (intent) {
            SplineAreaIntent.Shuffle -> setState {
                SplineAreaState(
                    earnings = SplineAreaSampleData.randomEarnings(Random.Default),
                    changePercent = SplineAreaSampleData.randomChangePercent(Random.Default),
                    trend = SplineAreaSampleData.randomTrend(Random.Default),
                )
            }
            SplineAreaIntent.Reset -> setState { SplineAreaState() }
        }
    }
}
