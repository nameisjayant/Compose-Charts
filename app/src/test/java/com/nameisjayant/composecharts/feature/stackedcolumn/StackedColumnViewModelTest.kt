package com.nameisjayant.composecharts.feature.stackedcolumn

import com.nameisjayant.composecharts.data.StackedColumnSampleData
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
class StackedColumnViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun startsOnTheDefaultMonth() {
        val state = StackedColumnViewModel().state.value

        assertEquals(StackedColumnSampleData.DEFAULT_MONTH, state.month)
        assertEquals(StackedColumnSampleData.apples[StackedColumnSampleData.DEFAULT_MONTH], state.apples)
        assertEquals(StackedColumnSampleData.bananas[StackedColumnSampleData.DEFAULT_MONTH], state.bananas)
    }

    @Test
    fun selectingAMonthShowsItsData() {
        val viewModel = StackedColumnViewModel()

        viewModel.onIntent(StackedColumnIntent.SelectMonth(3))

        val state = viewModel.state.value
        assertEquals(3, state.month)
        assertEquals(StackedColumnSampleData.apples[3], state.apples)
        assertEquals(StackedColumnSampleData.bananas[3], state.bananas)
    }

    @Test
    fun selectingAnUnknownMonthIsClamped() {
        val viewModel = StackedColumnViewModel()

        viewModel.onIntent(StackedColumnIntent.SelectMonth(99))

        assertEquals(StackedColumnSampleData.months.lastIndex, viewModel.state.value.month)
    }

    @Test
    fun shuffleKeepsTheMonthAndShape() {
        val viewModel = StackedColumnViewModel()
        viewModel.onIntent(StackedColumnIntent.SelectMonth(2))

        viewModel.onIntent(StackedColumnIntent.Shuffle)

        val state = viewModel.state.value
        assertEquals(2, state.month)
        assertEquals(StackedColumnSampleData.apples[0].size, state.apples.size)
        assertEquals(StackedColumnSampleData.bananas[0].size, state.bananas.size)
        assertTrue((state.apples + state.bananas).all { it in 15f..100f })
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = StackedColumnViewModel()

        viewModel.onIntent(StackedColumnIntent.SelectMonth(4))
        viewModel.onIntent(StackedColumnIntent.Shuffle)
        viewModel.onIntent(StackedColumnIntent.Reset)

        assertEquals(StackedColumnState(), viewModel.state.value)
    }

    @Test
    fun openingChatEmitsShowChat() = runTest(dispatcher) {
        val viewModel = StackedColumnViewModel()

        viewModel.onIntent(StackedColumnIntent.OpenChat)

        assertEquals(StackedColumnEffect.ShowChat(StackedColumnSampleData.NAME), viewModel.effect.first())
    }
}
