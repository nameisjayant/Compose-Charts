package com.nameisjayant.charts.core.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/** Collects one-off [UiEffect]s only while the screen is at least STARTED. */
@Composable
public fun <E : UiEffect> CollectEffect(
    effect: Flow<E>,
    onEffect: suspend (E) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnEffect = rememberUpdatedState(onEffect)
    LaunchedEffect(effect, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            effect.collect { latestOnEffect.value(it) }
        }
    }
}
