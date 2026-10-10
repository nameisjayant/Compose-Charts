package com.nameisjayant.charts.line

import org.junit.Assert.assertEquals
import org.junit.Test

class SplineAreaGeometryTest {

    @Test
    fun valuesSpreadFromEdgeToEdge() {
        val xs = List(5) { SplineAreaGeometry.x(it, count = 5, width = 400f) }

        assertEquals(listOf(0f, 100f, 200f, 300f, 400f), xs)
    }

    @Test
    fun loneValueSitsInTheMiddle() {
        assertEquals(150f, SplineAreaGeometry.x(0, count = 1, width = 300f), 0f)
    }

    @Test
    fun rangeFollowsTheDataUnlessOverridden() {
        assertEquals(120f to 255f, SplineAreaGeometry.valueRange(listOf(180f, 120f, 255f), null, null))
        assertEquals(0f to 300f, SplineAreaGeometry.valueRange(listOf(180f, 120f, 255f), 0f, 300f))
    }

    @Test
    fun flatOrEmptyDataGetsAUnitSpan() {
        assertEquals(4.5f to 5.5f, SplineAreaGeometry.valueRange(listOf(5f, 5f), null, null))
        assertEquals(0f to 1f, SplineAreaGeometry.valueRange(emptyList(), null, null))
    }
}
