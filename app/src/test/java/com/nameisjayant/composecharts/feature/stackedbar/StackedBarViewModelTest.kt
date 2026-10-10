package com.nameisjayant.composecharts.feature.stackedbar

import com.nameisjayant.composecharts.data.StackedBarSampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StackedBarViewModelTest {

    @Test
    fun startsWithSampleData() {
        val state = StackedBarViewModel().state.value

        assertEquals(StackedBarSampleData.store, state.store)
        assertEquals(StackedBarSampleData.web, state.web)
        assertEquals(StackedBarSampleData.app, state.app)
        assertEquals(StackedBarSampleData.CHANGE_PERCENT, state.changePercent)
    }

    @Test
    fun shuffleKeepsBarCountAndStaysInRange() {
        val viewModel = StackedBarViewModel()

        viewModel.onIntent(StackedBarIntent.Shuffle)

        val state = viewModel.state.value
        listOf(state.store, state.web, state.app).forEach { channel ->
            assertEquals(StackedBarSampleData.store.size, channel.size)
            assertTrue(channel.all { it in 5f..59f })
        }
        assertTrue(state.changePercent in -50..120)
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = StackedBarViewModel()

        viewModel.onIntent(StackedBarIntent.Shuffle)
        viewModel.onIntent(StackedBarIntent.Reset)

        assertEquals(StackedBarState(), viewModel.state.value)
    }
}
