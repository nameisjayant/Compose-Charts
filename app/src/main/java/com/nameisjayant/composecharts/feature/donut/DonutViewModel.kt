package com.nameisjayant.composecharts.feature.donut

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.DonutSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DonutViewModel @Inject constructor() :
    MviViewModel<DonutState, DonutIntent, DonutEffect>(DonutState()) {

    override fun handleIntent(intent: DonutIntent) {
        when (intent) {
            DonutIntent.Shuffle -> setState { copy(usedGb = DonutSampleData.random(Random.Default)) }
            DonutIntent.Reset -> setState { DonutState() }
        }
    }
}
