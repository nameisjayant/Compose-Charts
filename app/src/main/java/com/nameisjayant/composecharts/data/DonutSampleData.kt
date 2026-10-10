package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the donut sample: gigabytes used per file type on a 178 GB device. */
object DonutSampleData {
    const val CAPACITY_GB: Float = 178f

    val labels: List<String> = listOf("Documents", "Photos", "Music", "Videos")
    val usedGb: List<Float> = listOf(22f, 47f, 38f, 30f)

    /** New usage per file type, together never more than [CAPACITY_GB]. */
    fun random(random: Random): List<Float> = List(labels.size) { random.nextInt(5, 41).toFloat() }
}
