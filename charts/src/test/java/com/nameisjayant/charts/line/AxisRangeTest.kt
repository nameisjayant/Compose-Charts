package com.nameisjayant.charts.line

import org.junit.Assert.assertEquals
import org.junit.Test

class AxisRangeTest {

    @Test
    fun ticksRunFromMinToMaxByStep() {
        assertEquals(listOf(0f, 25f, 50f, 75f), AxisRange(min = 0f, max = 75f, step = 25f).ticks())
    }

    @Test
    fun ticksStopAtLastStepThatFits() {
        assertEquals(listOf(0f, 30f, 60f), AxisRange(min = 0f, max = 75f, step = 30f).ticks())
    }

    @Test
    fun niceRangeRoundsOutwardToRoundStep() {
        assertEquals(AxisRange(min = -80f, max = 0f, step = 20f), AxisRange.nice(-77f, -17f))
        assertEquals(AxisRange(min = 0f, max = 80f, step = 20f), AxisRange.nice(5f, 75f))
    }

    @Test
    fun niceRangeWidensSingleValue() {
        assertEquals(AxisRange(min = 2f, max = 4f, step = 0.5f), AxisRange.nice(3f, 3f))
    }

    @Test
    fun defaultFormatterDropsTrailingZeros() {
        val format = AreaRangeLineChartDefaults.LabelFormatter
        assertEquals("-80", format(-80f))
        assertEquals("2.5", format(2.5f))
        assertEquals("0.3", format(0.1f * 3))
    }
}
