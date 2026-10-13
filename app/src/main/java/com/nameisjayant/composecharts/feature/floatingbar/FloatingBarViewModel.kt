package com.nameisjayant.composecharts.feature.floatingbar

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.FloatingBarSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class FloatingBarViewModel @Inject constructor() :
    MviViewModel<FloatingBarState, FloatingBarIntent, FloatingBarEffect>(FloatingBarState()) {

    override fun handleIntent(intent: FloatingBarIntent) {
        when (intent) {
            is FloatingBarIntent.SelectRange -> {
                val range = intent.index.coerceIn(FloatingBarSampleData.ranges.indices)
                setState {
                    FloatingBarState(
                        range = range,
                        lows = FloatingBarSampleData.lows[range],
                        highs = FloatingBarSampleData.highs[range],
                    )
                }
            }
            FloatingBarIntent.Shuffle -> {
                val (lows, highs) = FloatingBarSampleData.randomBars(Random.Default)
                setState { copy(lows = lows, highs = highs) }
            }
            FloatingBarIntent.Reset -> setState { FloatingBarState() }
            FloatingBarIntent.OpenFilters -> sendEffect(FloatingBarEffect.ShowFilters)
        }
    }
}
