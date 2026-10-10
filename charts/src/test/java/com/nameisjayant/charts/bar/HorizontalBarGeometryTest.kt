package com.nameisjayant.charts.bar

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class HorizontalBarGeometryTest {

    @Test
    fun heightCoversBarsAndTheGapsBetweenThem() {
        assertEquals(152.dp, HorizontalBarGeometry.chartHeight(count = 5, barHeight = 24.dp, spacing = 8.dp))
        assertEquals(24.dp, HorizontalBarGeometry.chartHeight(count = 1, barHeight = 24.dp, spacing = 8.dp))
        assertEquals(0.dp, HorizontalBarGeometry.chartHeight(count = 0, barHeight = 24.dp, spacing = 8.dp))
    }

    @Test
    fun scaleIsTheLongestTrackOrFill() {
        assertEquals(80f, HorizontalBarGeometry.scale(values = listOf(30f, 56f), totals = listOf(60f, 80f), maxValue = null), 0f)
        assertEquals(90f, HorizontalBarGeometry.scale(values = listOf(90f), totals = listOf(60f), maxValue = null), 0f)
        assertEquals(0f, HorizontalBarGeometry.scale(values = emptyList(), totals = emptyList(), maxValue = null), 0f)
    }

    @Test
    fun explicitMaxValueWinsAndIsSanitised() {
        assertEquals(100f, HorizontalBarGeometry.scale(values = listOf(30f), totals = listOf(60f), maxValue = 100f), 0f)
        assertEquals(0f, HorizontalBarGeometry.scale(values = listOf(30f), totals = listOf(60f), maxValue = -5f), 0f)
    }

    @Test
    fun negativeAndNaNAreDrawnAsZero() {
        assertEquals(0f, HorizontalBarGeometry.sanitize(-3f), 0f)
        assertEquals(0f, HorizontalBarGeometry.sanitize(Float.NaN), 0f)
        assertEquals(7f, HorizontalBarGeometry.sanitize(7f), 0f)
    }
}
