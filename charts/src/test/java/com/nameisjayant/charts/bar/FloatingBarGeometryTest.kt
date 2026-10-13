package com.nameisjayant.charts.bar

import org.junit.Assert.assertEquals
import org.junit.Test

class FloatingBarGeometryTest {

    @Test
    fun maxIsRoundedUpToRoundSteps() {
        assertEquals(8000f, FloatingBarGeometry.niceMax(highest = 7800f, steps = 4), 0f)
        assertEquals(8000f, FloatingBarGeometry.niceMax(highest = 8000f, steps = 4), 0f)
        assertEquals(10f, FloatingBarGeometry.niceMax(highest = 9f, steps = 4), 1e-4f)
        assertEquals(400f, FloatingBarGeometry.niceMax(highest = 390f, steps = 4), 1e-3f)
        assertEquals(0f, FloatingBarGeometry.niceMax(highest = 0f, steps = 4), 0f)
    }

    @Test
    fun barsNarrowRatherThanTouch() {
        assertEquals(16f, FloatingBarGeometry.barWidth(count = 3, width = 200f, barWidth = 16f), 0f)
        assertEquals(10f, FloatingBarGeometry.barWidth(count = 3, width = 50f, barWidth = 16f), 0f)
        assertEquals(0f, FloatingBarGeometry.barWidth(count = 0, width = 50f, barWidth = 16f), 0f)
    }

    @Test
    fun barsSpanEdgeToEdge() {
        assertEquals(0f, FloatingBarGeometry.left(index = 0, count = 3, width = 100f, barWidth = 20f), 0f)
        assertEquals(40f, FloatingBarGeometry.left(index = 1, count = 3, width = 100f, barWidth = 20f), 0f)
        assertEquals(80f, FloatingBarGeometry.left(index = 2, count = 3, width = 100f, barWidth = 20f), 0f)
        assertEquals(40f, FloatingBarGeometry.left(index = 0, count = 1, width = 100f, barWidth = 20f), 0f)
    }

    @Test
    fun labelsUseThousands() {
        assertEquals("0", FloatingBarChartDefaults.formatValue(0f))
        assertEquals("500", FloatingBarChartDefaults.formatValue(500f))
        assertEquals("8k", FloatingBarChartDefaults.formatValue(8000f))
        assertEquals("2.5k", FloatingBarChartDefaults.formatValue(2500f))
    }
}
