package com.nameisjayant.composecharts.feature.doughnut

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.DoughnutSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DoughnutViewModel @Inject constructor() :
    MviViewModel<DoughnutState, DoughnutIntent, DoughnutEffect>(DoughnutState()) {

    override fun handleIntent(intent: DoughnutIntent) {
        when (intent) {
            DoughnutIntent.Shuffle -> setState { copy(shares = DoughnutSampleData.random(Random.Default)) }
            DoughnutIntent.Reset -> setState { DoughnutState() }
        }
    }
}
