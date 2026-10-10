package com.nameisjayant.charts.donut

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class DonutFrameTest {

    @Test
    fun fillsShareOfMaxValue() {
        val frame = DonutFrame.of(listOf(22f, 47f, 38f, 30f), maxValue = 178f)

        assertEquals(137f / 178f, frame.filledFraction, 1e-6f)
        assertEquals(360f * 137f / 178f, frame.sweeps().sum(), 1e-3f)
    }

    @Test
    fun withoutMaxValueSegmentsShareWholeRing() {
        val frame = DonutFrame.of(listOf(1f, 3f), maxValue = null)

        assertEquals(1f, frame.filledFraction, 0f)
        assertEquals(listOf(90f, 270f), frame.sweeps())
    }

    @Test
    fun cutsSegmentsOffAtFullRing() {
        val frame = DonutFrame.of(listOf(60f, 60f, 60f), maxValue = 100f)

        assertEquals(1f, frame.filledFraction, 0f)
        assertArrayEquals(floatArrayOf(216f, 144f, 0f), frame.sweeps().toFloatArray(), 1e-3f)
    }

    @Test
    fun treatsNegativeAndNaNAsZero() {
        val frame = DonutFrame.of(listOf(-5f, Float.NaN, 10f), maxValue = null)

        assertEquals(listOf(0f, 0f, 10f), frame.values)
    }
}
