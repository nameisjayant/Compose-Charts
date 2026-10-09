package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the vertical bar sample: one week of "data one" (up) vs "data two" (down). */
object VerticalBarSampleData {
    val upperValues: List<Float> = listOf(90f, 24f, 63f, 39f, 46f, 49f, 61f)
    val lowerValues: List<Float> = listOf(14f, 32f, 20f, 28f, 45f, 32f, 28f)

    fun randomUpper(random: Random): List<Float> = List(upperValues.size) { random.nextInt(10, 100).toFloat() }

    fun randomLower(random: Random): List<Float> = List(lowerValues.size) { random.nextInt(5, 50).toFloat() }
}
