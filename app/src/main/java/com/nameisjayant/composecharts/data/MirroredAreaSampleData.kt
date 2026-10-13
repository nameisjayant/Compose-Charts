package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the mirrored area sample: workout intensity per zone over a month. */
object MirroredAreaSampleData {
    const val MONTH: String = "September 2021"
    const val INTENSITY_PERCENT: Int = 80

    /** Series above the baseline, back to front: the tallest first so the smaller ones stay in view. */
    val upper: List<List<Float>> = listOf(
        listOf(60f, 30f, 75f, 85f, 40f, 100f, 45f, 70f, 98f, 85f, 40f, 65f, 100f, 55f, 70f, 35f, 45f, 60f),
        listOf(45f, 20f, 50f, 70f, 30f, 55f, 40f, 80f, 75f, 82f, 35f, 50f, 75f, 60f, 45f, 40f, 30f, 50f),
        listOf(30f, 15f, 40f, 45f, 25f, 35f, 30f, 60f, 55f, 65f, 25f, 40f, 45f, 50f, 35f, 30f, 25f, 40f),
        listOf(12f, 8f, 20f, 25f, 15f, 12f, 10f, 30f, 32f, 28f, 15f, 25f, 22f, 18f, 20f, 15f, 10f, 12f),
    )

    /** Series below the baseline, back to front. */
    val lower: List<List<Float>> = listOf(
        listOf(60f, 45f, 70f, 50f, 95f, 40f, 65f, 55f, 85f, 90f, 45f, 40f, 95f, 50f, 40f, 55f, 50f, 40f),
        listOf(50f, 70f, 40f, 60f, 50f, 70f, 45f, 75f, 70f, 60f, 50f, 65f, 50f, 60f, 35f, 55f, 60f, 50f),
        listOf(30f, 45f, 22f, 50f, 28f, 55f, 20f, 40f, 52f, 30f, 48f, 25f, 55f, 35f, 20f, 45f, 30f, 38f),
    )

    private val upperRanges: List<IntRange> = listOf(40..100, 25..80, 15..55, 5..30)
    private val lowerRanges: List<IntRange> = listOf(40..100, 30..75, 15..55)

    /** New upper series of the same shape, each staying within its layer's range. */
    fun randomUpper(random: Random): List<List<Float>> = upperRanges.map { randomValues(random, it) }

    /** New lower series of the same shape, each staying within its layer's range. */
    fun randomLower(random: Random): List<List<Float>> = lowerRanges.map { randomValues(random, it) }

    fun randomIntensity(random: Random): Int = random.nextInt(50, 96)

    private fun randomValues(random: Random, range: IntRange): List<Float> =
        List(upper[0].size) { random.nextInt(range.first, range.last + 1).toFloat() }
}
