package com.nameisjayant.charts.core.mvi

/** Immutable snapshot of everything a chart or screen renders. */
public interface UiState

/** A user action or event (tap, drag, data change, ...) sent to a store or ViewModel. */
public interface UiIntent

/** A one-off event (point selected, animation finished, ...) that must be consumed exactly once. */
public interface UiEffect
