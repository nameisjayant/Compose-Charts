package com.nameisjayant.composecharts.feature.stackedbar

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.StackedBarSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class StackedBarViewModel @Inject constructor() :
    MviViewModel<StackedBarState, StackedBarIntent, StackedBarEffect>(StackedBarState()) {

    override fun handleIntent(intent: StackedBarIntent) {
        when (intent) {
            StackedBarIntent.Shuffle -> setState {
                StackedBarState(
                    store = StackedBarSampleData.randomChannel(Random.Default),
                    web = StackedBarSampleData.randomChannel(Random.Default),
                    app = StackedBarSampleData.randomChannel(Random.Default),
                    changePercent = StackedBarSampleData.randomChangePercent(Random.Default),
                )
            }
            StackedBarIntent.Reset -> setState { StackedBarState() }
        }
    }
}
