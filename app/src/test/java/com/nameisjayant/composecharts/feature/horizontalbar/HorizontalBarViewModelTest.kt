package com.nameisjayant.composecharts.feature.horizontalbar

import com.nameisjayant.composecharts.data.HorizontalBarSampleData
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
class HorizontalBarViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun startsWithSampleData() {
        val state = HorizontalBarViewModel().state.value

        assertEquals(HorizontalBarSampleData.done, state.done)
        assertEquals(HorizontalBarSampleData.assigned, state.assigned)
    }

    @Test
    fun shuffleKeepsBarCountAndDoneWithinAssigned() {
        val viewModel = HorizontalBarViewModel()

        viewModel.onIntent(HorizontalBarIntent.Shuffle)

        val state = viewModel.state.value
        assertEquals(HorizontalBarSampleData.assigned.size, state.assigned.size)
        assertEquals(state.assigned.size, state.done.size)
        assertTrue(state.assigned.all { it in 25f..100f })
        state.done.zip(state.assigned).forEach { (done, assigned) -> assertTrue(done in 0f..assigned) }
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = HorizontalBarViewModel()

        viewModel.onIntent(HorizontalBarIntent.Shuffle)
        viewModel.onIntent(HorizontalBarIntent.Reset)

        assertEquals(HorizontalBarState(), viewModel.state.value)
    }

    @Test
    fun openingProfileEmitsShowProfile() = runTest(dispatcher) {
        val viewModel = HorizontalBarViewModel()

        viewModel.onIntent(HorizontalBarIntent.OpenProfile)

        assertEquals(HorizontalBarEffect.ShowProfile(HorizontalBarSampleData.NAME), viewModel.effect.first())
    }
}
