package com.nameisjayant.composecharts.feature.rankedbar

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.RankedBarSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class RankedBarViewModel @Inject constructor() :
    MviViewModel<RankedBarState, RankedBarIntent, RankedBarEffect>(RankedBarState()) {

    override fun handleIntent(intent: RankedBarIntent) {
        when (intent) {
            RankedBarIntent.Shuffle -> setState { RankedBarState(scores = RankedBarSampleData.randomScores(Random.Default)) }
            RankedBarIntent.Reset -> setState { RankedBarState() }
        }
    }
}
