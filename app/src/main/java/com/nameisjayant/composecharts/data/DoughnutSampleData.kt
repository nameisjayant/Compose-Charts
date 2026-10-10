package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the doughnut sample: percentage share of each stylesheet language on a site. */
object DoughnutSampleData {
    const val TOTAL: Float = 100f

    val labels: List<String> = listOf("css", "html", "sass")
    val shares: List<Float> = listOf(11f, 12f, 50f)

    /** New shares per language, together never more than [TOTAL]. */
    fun random(random: Random): List<Float> = List(labels.size) { random.nextInt(5, 34).toFloat() }
}
