package com.nameisjayant.charts.bar

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class RankedBarGeometryTest {

    @Test
    fun barsAreSortedLongestFirstKeepingTies() {
        val ranked = RankedBarGeometry.rank(
            listOf(RankedBar("a", 10f), RankedBar("b", 30f), RankedBar("c", 10f), RankedBar("d", Float.NaN)),
        )

        assertEquals(listOf("b", "a", "c", "d"), ranked.map { it.label })
    }

    @Test
    fun scaleIsTheLongestBarUnlessMaxValueIsGiven() {
        assertEquals(30f, RankedBarGeometry.scale(listOf(10f, 30f), maxValue = null), 0f)
        assertEquals(0f, RankedBarGeometry.scale(emptyList(), maxValue = null), 0f)
        assertEquals(50f, RankedBarGeometry.scale(listOf(10f, 30f), maxValue = 50f), 0f)
        assertEquals(0f, RankedBarGeometry.scale(listOf(10f), maxValue = -5f), 0f)
    }

    @Test
    fun onlyTheStaircaseOuterCornersAreRounded() {
        // top-left, top-right, bottom-right, bottom-left
        assertArrayEquals(booleanArrayOf(true, true, false, false), RankedBarGeometry.roundedCorners(0, 3))
        assertArrayEquals(booleanArrayOf(false, false, true, false), RankedBarGeometry.roundedCorners(1, 3))
        assertArrayEquals(booleanArrayOf(false, false, true, true), RankedBarGeometry.roundedCorners(2, 3))
        assertArrayEquals(booleanArrayOf(true, true, true, true), RankedBarGeometry.roundedCorners(0, 1))
    }
}
