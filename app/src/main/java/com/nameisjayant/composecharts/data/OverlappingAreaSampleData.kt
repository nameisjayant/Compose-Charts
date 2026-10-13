package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the overlapping area sample: a population index per region, for 2011 and 2012. */
object OverlappingAreaSampleData {
    const val NAME: String = "Ralph Edwards"
    const val ROLE: String = "Team Leader"

    val regions: List<String> = listOf("America", "Europe", "Asia")

    val first: List<List<Float>> = listOf(
        listOf(78f, 40f, 26f, 33f, 0f, 19f, 37f, 48f, 60f, 18f, 18f, 66f, 41f, 49f, 21f, 49f, 0f),
        listOf(52f, 61f, 44f, 70f, 38f, 22f, 30f, 57f, 41f, 64f, 35f, 28f, 50f, 72f, 46f, 31f, 40f),
        listOf(30f, 58f, 47f, 22f, 66f, 80f, 54f, 36f, 70f, 49f, 85f, 62f, 34f, 57f, 76f, 52f, 68f),
    )

    val second: List<List<Float>> = listOf(
        listOf(28f, 98f, 70f, 78f, 60f, 10f, 61f, 46f, 78f, 18f, 18f, 88f, 40f, 59f, 80f, 60f, 26f),
        listOf(70f, 45f, 82f, 55f, 66f, 90f, 48f, 36f, 74f, 52f, 60f, 84f, 39f, 58f, 69f, 81f, 55f),
        listOf(64f, 41f, 76f, 92f, 50f, 63f, 88f, 71f, 45f, 95f, 67f, 52f, 84f, 73f, 58f, 90f, 79f),
    )

    /** New values of the same length as each region's series. */
    fun randomValues(random: Random): List<Float> = List(first[0].size) { random.nextInt(0, 101).toFloat() }
}
