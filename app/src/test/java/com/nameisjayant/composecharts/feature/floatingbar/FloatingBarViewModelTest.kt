package com.nameisjayant.composecharts.feature.floatingbar

import com.nameisjayant.composecharts.data.FloatingBarSampleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FloatingBarViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun startsOnTheDefaultRange() {
        val state = FloatingBarViewModel().state.value

        assertEquals(FloatingBarSampleData.DEFAULT_RANGE, state.range)
        assertEquals(FloatingBarSampleData.lows[FloatingBarSampleData.DEFAULT_RANGE], state.lows)
        assertEquals(FloatingBarSampleData.highs[FloatingBarSampleData.DEFAULT_RANGE], state.highs)
    }

    @Test
    fun selectingARangeShowsItsData() {
        val viewModel = FloatingBarViewModel()

        viewModel.onIntent(FloatingBarIntent.SelectRange(4))

        val state = viewModel.state.value
        assertEquals(4, state.range)
        assertEquals(FloatingBarSampleData.lows[4], state.lows)
        assertEquals(FloatingBarSampleData.highs[4], state.highs)
    }

    @Test
    fun selectingAnUnknownRangeIsClamped() {
        val viewModel = FloatingBarViewModel()

        viewModel.onIntent(FloatingBarIntent.SelectRange(99))

        assertEquals(FloatingBarSampleData.ranges.lastIndex, viewModel.state.value.range)
    }

    @Test
    fun shuffleKeepsTheRangeAndShape() {
        val viewModel = FloatingBarViewModel()
        viewModel.onIntent(FloatingBarIntent.SelectRange(1))

        viewModel.onIntent(FloatingBarIntent.Shuffle)

        val state = viewModel.state.value
        assertEquals(1, state.range)
        assertEquals(FloatingBarSampleData.tones.size, state.lows.size)
        assertEquals(FloatingBarSampleData.tones.size, state.highs.size)
        assertTrue(state.lows.zip(state.highs).all { (low, high) -> high - low >= 1000f && high <= 8000f })
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = FloatingBarViewModel()

        viewModel.onIntent(FloatingBarIntent.SelectRange(5))
        viewModel.onIntent(FloatingBarIntent.Shuffle)
        viewModel.onIntent(FloatingBarIntent.Reset)

        assertEquals(FloatingBarState(), viewModel.state.value)
    }

    @Test
    fun openingFiltersEmitsShowFilters() = runTest(dispatcher) {
        val viewModel = FloatingBarViewModel()

        viewModel.onIntent(FloatingBarIntent.OpenFilters)

        assertEquals(FloatingBarEffect.ShowFilters, viewModel.effect.first())
    }
}
