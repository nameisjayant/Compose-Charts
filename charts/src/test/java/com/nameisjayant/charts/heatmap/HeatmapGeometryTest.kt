package com.nameisjayant.charts.heatmap

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class HeatmapGeometryTest {

    @Test
    fun emptyValuesTakeTheFirstLevel() {
        assertEquals(0, HeatmapGeometry.level(value = 0f, max = 12f, levels = 4))
        assertEquals(0, HeatmapGeometry.level(value = -3f, max = 12f, levels = 4))
        assertEquals(0, HeatmapGeometry.level(value = Float.NaN, max = 12f, levels = 4))
    }

    @Test
    fun valuesSplitEvenlyBetweenTheOtherLevels() {
        assertEquals(1, HeatmapGeometry.level(value = 1f, max = 12f, levels = 4))
        assertEquals(1, HeatmapGeometry.level(value = 4f, max = 12f, levels = 4))
        assertEquals(2, HeatmapGeometry.level(value = 5f, max = 12f, levels = 4))
        assertEquals(3, HeatmapGeometry.level(value = 12f, max = 12f, levels = 4))
        assertEquals(3, HeatmapGeometry.level(value = 40f, max = 12f, levels = 4))
    }

    @Test
    fun shortRowsArePaddedWithTheEmptyColour() {
        val colors = listOf(Color.White, Color.Gray, Color.Black)
        val shades = HeatmapGeometry.shades(listOf(listOf(0f, 2f), listOf(1f)), maxValue = null, colors = colors)

        assertEquals(listOf(listOf(Color.White, Color.Black), listOf(Color.Gray, Color.White)), shades)
    }

    @Test
    fun cellsShrinkToFit() {
        assertEquals(22f, HeatmapGeometry.cellSize(400f, 400f, 11, 6, 22f, 4f, 14f), 0f)
        assertEquals(10f, HeatmapGeometry.cellSize(64f, 400f, 4, 6, 22f, 8f, 14f), 0f)
        assertEquals(5f, HeatmapGeometry.cellSize(400f, 20f, 11, 2, 22f, 4f, 10f), 0f)
        assertEquals(0f, HeatmapGeometry.cellSize(400f, 400f, 0, 6, 22f, 4f, 14f), 0f)
    }

    @Test
    fun columnsSpanEdgeToEdge() {
        assertEquals(0f, HeatmapGeometry.left(index = 0, count = 3, width = 100f, cell = 20f), 0f)
        assertEquals(40f, HeatmapGeometry.left(index = 1, count = 3, width = 100f, cell = 20f), 0f)
        assertEquals(80f, HeatmapGeometry.left(index = 2, count = 3, width = 100f, cell = 20f), 0f)
        assertEquals(40f, HeatmapGeometry.left(index = 0, count = 1, width = 100f, cell = 20f), 0f)
    }

    @Test
    fun labelsSpreadFromFirstToLastColumn() {
        assertEquals(listOf(0, 2, 4, 6, 8, 10), List(6) { HeatmapGeometry.labelColumn(it, labelCount = 6, columns = 11) })
        assertEquals(0, HeatmapGeometry.labelColumn(0, labelCount = 1, columns = 11))
    }

    @Test
    fun columnsRevealLeftToRight() {
        assertEquals(0f, HeatmapGeometry.revealScale(reveal = 0f, column = 0, columns = 11), 0f)
        assertEquals(1f, HeatmapGeometry.revealScale(reveal = 0.5f, column = 0, columns = 11), 0f)
        assertEquals(0f, HeatmapGeometry.revealScale(reveal = 0.5f, column = 10, columns = 11), 0f)
        assertEquals(1f, HeatmapGeometry.revealScale(reveal = 1f, column = 10, columns = 11), 0f)
    }
}
