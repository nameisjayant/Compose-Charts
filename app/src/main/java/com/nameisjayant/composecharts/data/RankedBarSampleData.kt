package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the ranked bar sample: an economy score for eight countries, highest first. */
object RankedBarSampleData {
    val countries: List<String> = listOf(
        "Norway", "Australia", "Switzerland", "Netherlands",
        "United States", "Germany", "New Zealand", "Canada",
    )

    val scores: List<Float> = listOf(89f, 81f, 74f, 67f, 60f, 58f, 44f, 35f)

    /** New scores of the same length, in no particular order. */
    fun randomScores(random: Random): List<Float> = List(scores.size) { random.nextInt(20, 101).toFloat() }
}
