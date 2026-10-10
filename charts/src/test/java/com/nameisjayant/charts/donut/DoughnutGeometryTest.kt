package com.nameisjayant.charts.donut

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class DoughnutGeometryTest {

    @Test
    fun segmentsRunClockwiseAndFinishAtEndAngle() {
        val starts = DoughnutGeometry.startAngles(listOf(40f, 40f, 180f), endAngle = -90f)

        assertEquals(listOf(-350f, -310f, -270f), starts)
        assertEquals(-90f, starts.last() + 180f, 0f)
    }

    @Test
    fun segmentsGrowEvenlyToTheFullRadius() {
        val radii = DoughnutGeometry.outerRadiusFractions(count = 3, first = 0.8f)

        assertArrayEquals(floatArrayOf(0.8f, 0.9f, 1f), radii.toFloatArray(), 1e-6f)
    }

    @Test
    fun singleSegmentTakesTheFullRadius() {
        assertEquals(listOf(1f), DoughnutGeometry.outerRadiusFractions(count = 1, first = 0.5f))
        assertEquals(emptyList<Float>(), DoughnutGeometry.outerRadiusFractions(count = 0, first = 0.5f))
    }
}
