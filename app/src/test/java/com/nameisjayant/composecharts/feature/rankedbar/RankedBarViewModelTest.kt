package com.nameisjayant.composecharts.feature.rankedbar

import com.nameisjayant.composecharts.data.RankedBarSampleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RankedBarViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun startsWithSampleData() {
        assertEquals(RankedBarSampleData.scores, RankedBarViewModel().state.value.scores)
    }

    @Test
    fun shuffleKeepsOneScorePerCountry() {
        val viewModel = RankedBarViewModel()

        viewModel.onIntent(RankedBarIntent.Shuffle)

        val scores = viewModel.state.value.scores
        assertEquals(RankedBarSampleData.countries.size, scores.size)
        assertTrue(scores.all { it in 20f..100f })
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = RankedBarViewModel()

        viewModel.onIntent(RankedBarIntent.Shuffle)
        viewModel.onIntent(RankedBarIntent.Reset)

        assertEquals(RankedBarState(), viewModel.state.value)
    }
}
