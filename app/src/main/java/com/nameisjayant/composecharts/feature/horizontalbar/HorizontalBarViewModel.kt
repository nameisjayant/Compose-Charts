package com.nameisjayant.composecharts.feature.horizontalbar

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.HorizontalBarSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HorizontalBarViewModel @Inject constructor() :
    MviViewModel<HorizontalBarState, HorizontalBarIntent, HorizontalBarEffect>(HorizontalBarState()) {

    override fun handleIntent(intent: HorizontalBarIntent) {
        when (intent) {
            HorizontalBarIntent.Shuffle -> setState {
                val assigned = HorizontalBarSampleData.randomAssigned(Random.Default)
                HorizontalBarState(
                    done = HorizontalBarSampleData.randomDone(assigned, Random.Default),
                    assigned = assigned,
                )
            }
            HorizontalBarIntent.Reset -> setState { HorizontalBarState() }
            HorizontalBarIntent.OpenProfile -> sendEffect(HorizontalBarEffect.ShowProfile(HorizontalBarSampleData.NAME))
        }
    }
}
