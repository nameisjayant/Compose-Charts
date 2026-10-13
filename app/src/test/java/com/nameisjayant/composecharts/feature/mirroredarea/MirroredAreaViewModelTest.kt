package com.nameisjayant.composecharts.feature.mirroredarea

import com.nameisjayant.composecharts.data.MirroredAreaSampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MirroredAreaViewModelTest {

    @Test
    fun startsWithSampleData() {
        val state = MirroredAreaViewModel().state.value

        assertEquals(MirroredAreaSampleData.upper, state.upper)
        assertEquals(MirroredAreaSampleData.lower, state.lower)
        assertEquals(MirroredAreaSampleData.INTENSITY_PERCENT, state.intensityPercent)
    }

    @Test
    fun shuffleKeepsTheShapeOfTheData() {
        val viewModel = MirroredAreaViewModel()

        viewModel.onIntent(MirroredAreaIntent.Shuffle)

        val state = viewModel.state.value
        assertEquals(MirroredAreaSampleData.upper.map { it.size }, state.upper.map { it.size })
        assertEquals(MirroredAreaSampleData.lower.map { it.size }, state.lower.map { it.size })
        assertTrue((state.upper + state.lower).flatten().all { it in 0f..100f })
        assertTrue(state.intensityPercent in 50..95)
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = MirroredAreaViewModel()

        viewModel.onIntent(MirroredAreaIntent.Shuffle)
        viewModel.onIntent(MirroredAreaIntent.Reset)

        assertEquals(MirroredAreaState(), viewModel.state.value)
    }
}
