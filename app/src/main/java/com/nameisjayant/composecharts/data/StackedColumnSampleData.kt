package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the stacked column sample: weekly fruit sales per month, apples beside bananas. */
object StackedColumnSampleData {
    const val NAME: String = "Jerome Bell"
    const val ROLE: String = "Marketing Coordinator"
    const val DEFAULT_MONTH: Int = 1

    val months: List<String> = listOf("Nov", "Dec", "Jan", "Feb", "Mar", "Apr")

    /** Apples sold each week, one list per month. */
    val apples: List<List<Float>> = listOf(
        listOf(62f, 80f, 45f, 70f, 38f),
        listOf(100f, 73f, 55f, 85f, 27f),
        listOf(48f, 66f, 90f, 52f, 74f),
        listOf(35f, 58f, 72f, 95f, 60f),
        listOf(82f, 40f, 64f, 50f, 88f),
        listOf(55f, 92f, 38f, 76f, 68f),
    )

    /** Bananas sold each week, one list per month. */
    val bananas: List<List<Float>> = listOf(
        listOf(54f, 92f, 40f, 61f, 47f),
        listOf(100f, 73f, 55f, 85f, 27f),
        listOf(60f, 58f, 82f, 44f, 86f),
        listOf(42f, 50f, 88f, 79f, 52f),
        listOf(70f, 52f, 58f, 64f, 96f),
        listOf(63f, 80f, 46f, 90f, 57f),
    )

    /** New weekly values of the same length as each month's. */
    fun randomValues(random: Random): List<Float> = List(apples[0].size) { random.nextInt(15, 101).toFloat() }
}
