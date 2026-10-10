package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the spline area sample: expected earnings (€) and their recent trend. */
object SplineAreaSampleData {
    const val EARNINGS: Float = 682.5f
    const val CHANGE_PERCENT: Float = 2.45f

    val trend: List<Float> = listOf(180f, 148f, 194f, 184f, 255f, 120f, 200f, 248f, 194f, 200f, 140f, 196f, 192f, 248f)

    /** A new trend of the same length. */
    fun randomTrend(random: Random): List<Float> = List(trend.size) { random.nextInt(110, 260).toFloat() }

    /** Earnings rounded to one decimal, like the headline shows them. */
    fun randomEarnings(random: Random): Float = random.nextInt(4_000, 9_000) / 10f

    /** A change between -5% and +5%, rounded to two decimals. */
    fun randomChangePercent(random: Random): Float = random.nextInt(-500, 501) / 100f
}
