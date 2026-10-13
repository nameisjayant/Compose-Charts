package com.nameisjayant.composecharts.feature.heatmap

import com.nameisjayant.composecharts.data.HeatmapSampleData
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
class HeatmapViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun startsOnTheDefaultYear() {
        val state = HeatmapViewModel().state.value

        assertEquals(HeatmapSampleData.DEFAULT_YEAR, state.year)
        assertEquals(HeatmapSampleData.values[HeatmapSampleData.DEFAULT_YEAR], state.values)
    }

    @Test
    fun totalAddsUpEveryCell() {
        assertEquals(193, HeatmapState().total)
    }

    @Test
    fun selectingAYearShowsItsData() {
        val viewModel = HeatmapViewModel()

        viewModel.onIntent(HeatmapIntent.SelectYear(2))

        val state = viewModel.state.value
        assertEquals(2, state.year)
        assertEquals(HeatmapSampleData.values[2], state.values)
    }

    @Test
    fun selectingAnUnknownYearIsClamped() {
        val viewModel = HeatmapViewModel()

        viewModel.onIntent(HeatmapIntent.SelectYear(99))

        assertEquals(HeatmapSampleData.years.lastIndex, viewModel.state.value.year)
    }

    @Test
    fun shuffleKeepsTheYearAndShape() {
        val viewModel = HeatmapViewModel()
        viewModel.onIntent(HeatmapIntent.SelectYear(1))

        viewModel.onIntent(HeatmapIntent.Shuffle)

        val state = viewModel.state.value
        val sample = HeatmapSampleData.values[1]
        assertEquals(1, state.year)
        assertEquals(sample.map { it.size }, state.values.map { it.size })
        assertTrue(state.values.flatten().all { it in 0f..14f })
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = HeatmapViewModel()

        viewModel.onIntent(HeatmapIntent.SelectYear(3))
        viewModel.onIntent(HeatmapIntent.Shuffle)
        viewModel.onIntent(HeatmapIntent.Reset)

        assertEquals(HeatmapState(), viewModel.state.value)
    }

    @Test
    fun openingSettingsEmitsShowSettings() = runTest(dispatcher) {
        val viewModel = HeatmapViewModel()

        viewModel.onIntent(HeatmapIntent.OpenSettings)

        assertEquals(HeatmapEffect.ShowSettings, viewModel.effect.first())
    }

    @Test
    fun showingMoreActivityEmitsShowActivity() = runTest(dispatcher) {
        val viewModel = HeatmapViewModel()

        viewModel.onIntent(HeatmapIntent.ShowMoreActivity)

        assertEquals(HeatmapEffect.ShowActivity, viewModel.effect.first())
    }
}
