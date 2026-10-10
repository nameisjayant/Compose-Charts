package com.nameisjayant.charts.bar

import org.junit.Assert.assertEquals
import org.junit.Test

class StackedBarGeometryTest {

    @Test
    fun barsShareTheWidthLeftAfterSpacing() {
        assertEquals(40f, StackedBarGeometry.barWidth(count = 4, width = 190f, spacing = 10f, maxBarWidth = Float.POSITIVE_INFINITY), 0f)
    }

    @Test
    fun barWidthIsCappedAndNeverNegative() {
        assertEquals(20f, StackedBarGeometry.barWidth(count = 4, width = 190f, spacing = 10f, maxBarWidth = 20f), 0f)
        assertEquals(0f, StackedBarGeometry.barWidth(count = 4, width = 20f, spacing = 10f, maxBarWidth = 20f), 0f)
        assertEquals(0f, StackedBarGeometry.barWidth(count = 0, width = 100f, spacing = 10f, maxBarWidth = 20f), 0f)
    }

    @Test
    fun barsRunFromEdgeToEdge() {
        val lefts = List(3) { StackedBarGeometry.left(it, count = 3, width = 220f, barWidth = 20f) }

        assertEquals(listOf(0f, 100f, 200f), lefts)
    }

    @Test
    fun loneBarIsCentred() {
        assertEquals(40f, StackedBarGeometry.left(0, count = 1, width = 100f, barWidth = 20f), 0f)
    }

    @Test
    fun totalsAddEverySeriesAndPadShortOnes() {
        val totals = StackedBarGeometry.totals(listOf(listOf(1f, 2f, 3f), listOf(4f, 5f), listOf(6f)))

        assertEquals(listOf(11f, 7f, 3f), totals)
    }
}
