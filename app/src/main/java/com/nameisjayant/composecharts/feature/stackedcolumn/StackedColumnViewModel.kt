package com.nameisjayant.composecharts.feature.stackedcolumn

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.StackedColumnSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class StackedColumnViewModel @Inject constructor() :
    MviViewModel<StackedColumnState, StackedColumnIntent, StackedColumnEffect>(StackedColumnState()) {

    override fun handleIntent(intent: StackedColumnIntent) {
        when (intent) {
            is StackedColumnIntent.SelectMonth -> {
                val month = intent.index.coerceIn(StackedColumnSampleData.months.indices)
                setState {
                    StackedColumnState(
                        month = month,
                        apples = StackedColumnSampleData.apples[month],
                        bananas = StackedColumnSampleData.bananas[month],
                    )
                }
            }
            StackedColumnIntent.Shuffle -> setState {
                copy(
                    apples = StackedColumnSampleData.randomValues(Random.Default),
                    bananas = StackedColumnSampleData.randomValues(Random.Default),
                )
            }
            StackedColumnIntent.Reset -> setState { StackedColumnState() }
            StackedColumnIntent.OpenChat -> sendEffect(StackedColumnEffect.ShowChat(StackedColumnSampleData.NAME))
        }
    }
}
