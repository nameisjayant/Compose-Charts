package com.nameisjayant.charts.core.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MviStoreTest {

    private data class CounterState(val count: Int = 0) : UiState

    private sealed interface CounterIntent : UiIntent {
        data object Increment : CounterIntent
        data object Reset : CounterIntent
    }

    private sealed interface CounterEffect : UiEffect {
        data object WasReset : CounterEffect
    }

    private class CounterStore(scope: CoroutineScope) :
        MviStore<CounterState, CounterIntent, CounterEffect>(CounterState(), scope) {
        override fun handleIntent(intent: CounterIntent) {
            when (intent) {
                CounterIntent.Increment -> setState { copy(count = count + 1) }
                CounterIntent.Reset -> {
                    setState { CounterState() }
                    sendEffect(CounterEffect.WasReset)
                }
            }
        }
    }

    @Test
    fun intentsAreReducedIntoState() = runTest {
        val store = CounterStore(backgroundScope)

        store.onIntent(CounterIntent.Increment)
        store.onIntent(CounterIntent.Increment)

        assertEquals(2, store.state.value.count)
    }

    @Test
    fun effectsAreEmittedOnce() = runTest {
        val store = CounterStore(backgroundScope)

        store.onIntent(CounterIntent.Increment)
        store.onIntent(CounterIntent.Reset)

        assertEquals(CounterEffect.WasReset, store.effect.first())
        assertEquals(0, store.state.value.count)
    }
}
