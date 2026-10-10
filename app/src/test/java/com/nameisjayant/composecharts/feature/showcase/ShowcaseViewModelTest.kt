package com.nameisjayant.composecharts.feature.showcase

import com.nameisjayant.composecharts.data.ChartSample
import com.nameisjayant.composecharts.data.ChartSampleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShowcaseViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun loadsSamplesOnStart() = runTest(dispatcher) {
        val viewModel = ShowcaseViewModel(ChartSampleRepository(dispatcher))

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(
            listOf(ChartSample.VERTICAL_BAR, ChartSample.AREA_RANGE_LINE, ChartSample.DONUT, ChartSample.DOUGHNUT,
                ChartSample.SPLINE_AREA, ChartSample.STACKED_BAR, ChartSample.HORIZONTAL_BAR),
            viewModel.state.value.samples.map { it.id })
    }

    @Test
    fun openingSampleEmitsNavigation() = runTest(dispatcher) {
        val viewModel = ShowcaseViewModel(ChartSampleRepository(dispatcher))

        viewModel.onIntent(ShowcaseIntent.OpenSample("line"))

        assertEquals(ShowcaseEffect.NavigateToSample("line"), viewModel.effect.first())
    }
}
