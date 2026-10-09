package com.nameisjayant.composecharts.data

import com.nameisjayant.charts.line.AxisRange
import com.nameisjayant.charts.line.LinePoint
import kotlin.random.Random

/** Demo values for the area range sample: average July temperature (°C) at each altitude (km). */
object AreaRangeSampleData {
    val points: List<LinePoint> = listOf(
        LinePoint(x = -77f, y = 75f),
        LinePoint(x = -39f, y = 53f),
        LinePoint(x = -66f, y = 24f),
        LinePoint(x = -40f, y = 14f),
        LinePoint(x = -17f, y = 5f),
    )

    val temperatureRange: AxisRange = AxisRange(min = -80f, max = -10f, step = 10f)
    val altitudeRange: AxisRange = AxisRange(min = 0f, max = 75f, step = 25f)

    /** Same altitudes, new temperatures kept clear of the axis edges. */
    fun random(random: Random): List<LinePoint> = points.map { it.copy(x = random.nextInt(-76, -15).toFloat()) }
}
