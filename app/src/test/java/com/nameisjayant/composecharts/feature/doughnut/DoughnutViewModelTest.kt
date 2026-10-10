package com.nameisjayant.composecharts.feature.doughnut

import com.nameisjayant.composecharts.data.DoughnutSampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DoughnutViewModelTest {

    @Test
    fun startsWithSampleData() {
        val viewModel = DoughnutViewModel()

        assertEquals(DoughnutSampleData.shares, viewModel.state.value.shares)
        assertEquals(73f, viewModel.state.value.shares.sum())
    }

    @Test
    fun shuffleKeepsSegmentCountAndFitsTotal() {
        val viewModel = DoughnutViewModel()

        viewModel.onIntent(DoughnutIntent.Shuffle)

        val shares = viewModel.state.value.shares
        assertEquals(DoughnutSampleData.labels.size, shares.size)
        assertTrue(shares.all { it in 5f..33f })
        assertTrue(shares.sum() <= DoughnutSampleData.TOTAL)
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = DoughnutViewModel()

        viewModel.onIntent(DoughnutIntent.Shuffle)
        viewModel.onIntent(DoughnutIntent.Reset)

        assertEquals(DoughnutState(), viewModel.state.value)
    }
}
