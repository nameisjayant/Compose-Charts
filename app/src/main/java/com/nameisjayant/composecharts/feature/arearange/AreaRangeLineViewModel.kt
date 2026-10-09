package com.nameisjayant.composecharts.feature.arearange

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.AreaRangeSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AreaRangeLineViewModel @Inject constructor() :
    MviViewModel<AreaRangeLineState, AreaRangeLineIntent, AreaRangeLineEffect>(AreaRangeLineState()) {

    override fun handleIntent(intent: AreaRangeLineIntent) {
        when (intent) {
            AreaRangeLineIntent.Shuffle -> setState { copy(points = AreaRangeSampleData.random(Random.Default)) }
            AreaRangeLineIntent.Reset -> setState { AreaRangeLineState() }
        }
    }
}
