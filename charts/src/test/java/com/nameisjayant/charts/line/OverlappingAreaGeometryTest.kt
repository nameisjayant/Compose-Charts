package com.nameisjayant.charts.line

import org.junit.Assert.assertEquals
import org.junit.Test

class OverlappingAreaGeometryTest {

    @Test
    fun valuesSpreadEdgeToEdge() {
        assertEquals(0f, OverlappingAreaGeometry.x(0, 5, 200f), 0f)
        assertEquals(100f, OverlappingAreaGeometry.x(2, 5, 200f), 0f)
        assertEquals(200f, OverlappingAreaGeometry.x(4, 5, 200f), 0f)
        assertEquals(0f, OverlappingAreaGeometry.x(0, 1, 200f), 0f)
    }

    @Test
    fun scaleIsTheLargestValueOfAnySeriesUnlessMaxValueIsGiven() {
        assertEquals(40f, OverlappingAreaGeometry.scale(listOf(listOf(10f, 30f), listOf(40f, 5f)), maxValue = null), 0f)
        assertEquals(0f, OverlappingAreaGeometry.scale(listOf(emptyList()), maxValue = null), 0f)
        assertEquals(0f, OverlappingAreaGeometry.scale(emptyList(), maxValue = null), 0f)
        assertEquals(50f, OverlappingAreaGeometry.scale(listOf(listOf(10f)), maxValue = 50f), 0f)
        assertEquals(0f, OverlappingAreaGeometry.scale(listOf(listOf(10f)), maxValue = -5f), 0f)
    }

    @Test
    fun fractionIsClampedToTheChart() {
        assertEquals(0.5f, OverlappingAreaGeometry.fraction(20f, 40f), 0f)
        assertEquals(1f, OverlappingAreaGeometry.fraction(80f, 40f), 0f)
        assertEquals(0f, OverlappingAreaGeometry.fraction(20f, 0f), 0f)
    }

    @Test
    fun negativeAndNanValuesAreZero() {
        assertEquals(0f, OverlappingAreaGeometry.sanitize(-3f), 0f)
        assertEquals(0f, OverlappingAreaGeometry.sanitize(Float.NaN), 0f)
        assertEquals(7f, OverlappingAreaGeometry.sanitize(7f), 0f)
    }
}
