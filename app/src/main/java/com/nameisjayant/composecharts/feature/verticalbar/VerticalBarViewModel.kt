package com.nameisjayant.composecharts.feature.verticalbar

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.VerticalBarSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class VerticalBarViewModel @Inject constructor() :
    MviViewModel<VerticalBarState, VerticalBarIntent, VerticalBarEffect>(VerticalBarState()) {

    override fun handleIntent(intent: VerticalBarIntent) {
        when (intent) {
            VerticalBarIntent.Shuffle -> setState {
                copy(
                    upperValues = VerticalBarSampleData.randomUpper(Random.Default),
                    lowerValues = VerticalBarSampleData.randomLower(Random.Default),
                )
            }
            VerticalBarIntent.Reset -> setState { VerticalBarState() }
        }
    }
}
