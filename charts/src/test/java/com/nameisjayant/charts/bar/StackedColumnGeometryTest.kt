package com.nameisjayant.charts.bar

import org.junit.Assert.assertEquals
import org.junit.Test

class StackedColumnGeometryTest {

    @Test
    fun columnsShareTheWidthLeftAfterSpacing() {
        assertEquals(20f, StackedColumnGeometry.columnWidth(count = 4, width = 110f, spacing = 10f, maxColumnWidth = Float.POSITIVE_INFINITY), 0f)
        assertEquals(12f, StackedColumnGeometry.columnWidth(count = 4, width = 110f, spacing = 10f, maxColumnWidth = 12f), 0f)
        assertEquals(0f, StackedColumnGeometry.columnWidth(count = 0, width = 110f, spacing = 10f, maxColumnWidth = 12f), 0f)
        assertEquals(0f, StackedColumnGeometry.columnWidth(count = 4, width = 10f, spacing = 10f, maxColumnWidth = 12f), 0f)
    }

    @Test
    fun stripesSplitTheColumn() {
        assertEquals(24f, StackedColumnGeometry.stripeWidth(seriesCount = 2, columnWidth = 48f, spacing = 0f), 0f)
        assertEquals(22f, StackedColumnGeometry.stripeWidth(seriesCount = 2, columnWidth = 48f, spacing = 4f), 0f)
        assertEquals(0f, StackedColumnGeometry.stripeWidth(seriesCount = 0, columnWidth = 48f, spacing = 0f), 0f)
    }

    @Test
    fun columnsSpanEdgeToEdge() {
        assertEquals(0f, StackedColumnGeometry.left(index = 0, count = 3, width = 100f, columnWidth = 20f), 0f)
        assertEquals(40f, StackedColumnGeometry.left(index = 1, count = 3, width = 100f, columnWidth = 20f), 0f)
        assertEquals(80f, StackedColumnGeometry.left(index = 2, count = 3, width = 100f, columnWidth = 20f), 0f)
        assertEquals(40f, StackedColumnGeometry.left(index = 0, count = 1, width = 100f, columnWidth = 20f), 0f)
    }
}
