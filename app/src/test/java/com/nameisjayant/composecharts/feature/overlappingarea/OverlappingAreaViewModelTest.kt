package com.nameisjayant.composecharts.feature.overlappingarea

import com.nameisjayant.composecharts.data.OverlappingAreaSampleData
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
class OverlappingAreaViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun startsWithTheFirstRegion() {
        val state = OverlappingAreaViewModel().state.value

        assertEquals(0, state.region)
        assertEquals(OverlappingAreaSampleData.first[0], state.first)
        assertEquals(OverlappingAreaSampleData.second[0], state.second)
    }

    @Test
    fun selectingARegionShowsItsData() {
        val viewModel = OverlappingAreaViewModel()

        viewModel.onIntent(OverlappingAreaIntent.SelectRegion(2))

        val state = viewModel.state.value
        assertEquals(2, state.region)
        assertEquals(OverlappingAreaSampleData.first[2], state.first)
        assertEquals(OverlappingAreaSampleData.second[2], state.second)
    }

    @Test
    fun shuffleKeepsRegionAndSeriesLength() {
        val viewModel = OverlappingAreaViewModel()
        viewModel.onIntent(OverlappingAreaIntent.SelectRegion(1))

        viewModel.onIntent(OverlappingAreaIntent.Shuffle)

        val state = viewModel.state.value
        assertEquals(1, state.region)
        assertEquals(OverlappingAreaSampleData.first[1].size, state.first.size)
        assertEquals(OverlappingAreaSampleData.second[1].size, state.second.size)
        assertTrue((state.first + state.second).all { it in 0f..100f })
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = OverlappingAreaViewModel()

        viewModel.onIntent(OverlappingAreaIntent.SelectRegion(2))
        viewModel.onIntent(OverlappingAreaIntent.Shuffle)
        viewModel.onIntent(OverlappingAreaIntent.Reset)

        assertEquals(OverlappingAreaState(), viewModel.state.value)
    }

    @Test
    fun openingChatEmitsShowChat() = runTest(dispatcher) {
        val viewModel = OverlappingAreaViewModel()

        viewModel.onIntent(OverlappingAreaIntent.OpenChat)

        assertEquals(OverlappingAreaEffect.ShowChat(OverlappingAreaSampleData.NAME), viewModel.effect.first())
    }
}
