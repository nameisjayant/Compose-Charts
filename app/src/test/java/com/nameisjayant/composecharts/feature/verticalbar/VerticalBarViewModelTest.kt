package com.nameisjayant.composecharts.feature.verticalbar

import com.nameisjayant.composecharts.data.VerticalBarSampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VerticalBarViewModelTest {

    @Test
    fun startsWithSampleData() {
        val viewModel = VerticalBarViewModel()

        assertEquals(VerticalBarSampleData.upperValues, viewModel.state.value.upperValues)
        assertEquals(VerticalBarSampleData.lowerValues, viewModel.state.value.lowerValues)
    }

    @Test
    fun shuffleKeepsBarCountAndRanges() {
        val viewModel = VerticalBarViewModel()

        viewModel.onIntent(VerticalBarIntent.Shuffle)

        val state = viewModel.state.value
        assertEquals(VerticalBarSampleData.upperValues.size, state.upperValues.size)
        assertEquals(VerticalBarSampleData.lowerValues.size, state.lowerValues.size)
        assertTrue(state.upperValues.all { it in 10f..99f })
        assertTrue(state.lowerValues.all { it in 5f..49f })
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = VerticalBarViewModel()

        viewModel.onIntent(VerticalBarIntent.Shuffle)
        viewModel.onIntent(VerticalBarIntent.Reset)

        assertEquals(VerticalBarState(), viewModel.state.value)
    }
}
