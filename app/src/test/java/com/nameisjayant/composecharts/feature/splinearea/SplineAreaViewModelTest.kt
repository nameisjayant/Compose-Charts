package com.nameisjayant.composecharts.feature.splinearea

import com.nameisjayant.composecharts.data.SplineAreaSampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SplineAreaViewModelTest {

    @Test
    fun startsWithSampleData() {
        val state = SplineAreaViewModel().state.value

        assertEquals(SplineAreaSampleData.EARNINGS, state.earnings)
        assertEquals(SplineAreaSampleData.CHANGE_PERCENT, state.changePercent)
        assertEquals(SplineAreaSampleData.trend, state.trend)
    }

    @Test
    fun shuffleKeepsTrendLengthAndStaysInRange() {
        val viewModel = SplineAreaViewModel()

        viewModel.onIntent(SplineAreaIntent.Shuffle)

        val state = viewModel.state.value
        assertEquals(SplineAreaSampleData.trend.size, state.trend.size)
        assertTrue(state.trend.all { it in 110f..259f })
        assertTrue(state.earnings in 400f..900f)
        assertTrue(state.changePercent in -5f..5f)
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = SplineAreaViewModel()

        viewModel.onIntent(SplineAreaIntent.Shuffle)
        viewModel.onIntent(SplineAreaIntent.Reset)

        assertEquals(SplineAreaState(), viewModel.state.value)
    }
}
