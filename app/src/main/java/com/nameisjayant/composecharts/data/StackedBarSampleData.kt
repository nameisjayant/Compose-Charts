package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the stacked bar sample: eight days of orders, split by channel, and the change over the period. */
object StackedBarSampleData {
    const val CHANGE_PERCENT: Int = 86

    val store: List<Float> = listOf(70f, 40f, 22f, 40f, 17f, 21f, 13f, 8f)
    val web: List<Float> = listOf(30f, 62f, 55f, 28f, 15f, 19f, 10f, 6f)
    val app: List<Float> = listOf(30f, 18f, 29f, 15f, 30f, 10f, 17f, 40f)

    /** A new channel of the same length. */
    fun randomChannel(random: Random): List<Float> = List(store.size) { random.nextInt(5, 60).toFloat() }

    /** A change between -50% and +120%. */
    fun randomChangePercent(random: Random): Int = random.nextInt(-50, 121)
}
