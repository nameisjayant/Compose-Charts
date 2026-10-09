package com.nameisjayant.charts.core.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Lifecycle-agnostic MVI state holder used by the charts.
 *
 * It mirrors a ViewModel without depending on one, so a chart can own its interaction state
 * (selection, zoom, scroll) through `remember` while apps keep their own ViewModels.
 *
 * Data flows one way: callers send [UiIntent]s through [onIntent], the store reduces them into a
 * new [UiState] exposed via [state], and one-off events are emitted through [effect].
 */
public abstract class MviStore<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S,
    protected val scope: CoroutineScope,
) {

    private val _state = MutableStateFlow(initialState)
    public val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    public val effect: Flow<E> = _effect.receiveAsFlow()

    protected val currentState: S
        get() = _state.value

    public fun onIntent(intent: I): Unit = handleIntent(intent)

    protected abstract fun handleIntent(intent: I)

    protected fun setState(reducer: S.() -> S): Unit = _state.update(reducer)

    protected fun sendEffect(effect: E) {
        scope.launch { _effect.send(effect) }
    }
}
