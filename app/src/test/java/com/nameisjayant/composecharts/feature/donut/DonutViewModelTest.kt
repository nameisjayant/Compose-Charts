package com.nameisjayant.composecharts.feature.donut

import com.nameisjayant.composecharts.data.DonutSampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DonutViewModelTest {

    @Test
    fun startsWithSampleData() {
        val viewModel = DonutViewModel()

        assertEquals(DonutSampleData.usedGb, viewModel.state.value.usedGb)
        assertEquals(137f, viewModel.state.value.totalUsedGb)
    }

    @Test
    fun shuffleKeepsSegmentCountAndFitsCapacity() {
        val viewModel = DonutViewModel()

        viewModel.onIntent(DonutIntent.Shuffle)

        val state = viewModel.state.value
        assertEquals(DonutSampleData.labels.size, state.usedGb.size)
        assertTrue(state.usedGb.all { it in 5f..40f })
        assertTrue(state.totalUsedGb <= DonutSampleData.CAPACITY_GB)
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = DonutViewModel()

        viewModel.onIntent(DonutIntent.Shuffle)
        viewModel.onIntent(DonutIntent.Reset)

        assertEquals(DonutState(), viewModel.state.value)
    }
}
