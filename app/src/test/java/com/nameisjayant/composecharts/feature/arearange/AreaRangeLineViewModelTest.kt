package com.nameisjayant.composecharts.feature.arearange

import com.nameisjayant.composecharts.data.AreaRangeSampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AreaRangeLineViewModelTest {

    @Test
    fun startsWithSampleData() {
        val viewModel = AreaRangeLineViewModel()

        assertEquals(AreaRangeSampleData.points, viewModel.state.value.points)
    }

    @Test
    fun shuffleKeepsAltitudesAndStaysInsideTemperatureAxis() {
        val viewModel = AreaRangeLineViewModel()

        viewModel.onIntent(AreaRangeLineIntent.Shuffle)

        val points = viewModel.state.value.points
        assertEquals(AreaRangeSampleData.points.map { it.y }, points.map { it.y })
        val axis = AreaRangeSampleData.temperatureRange
        assertTrue(points.all { it.x in axis.min..axis.max })
    }

    @Test
    fun resetRestoresSampleData() {
        val viewModel = AreaRangeLineViewModel()

        viewModel.onIntent(AreaRangeLineIntent.Shuffle)
        viewModel.onIntent(AreaRangeLineIntent.Reset)

        assertEquals(AreaRangeLineState(), viewModel.state.value)
    }
}
