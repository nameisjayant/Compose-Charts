package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the horizontal bar sample: one team leader's tasks done out of tasks assigned, per sprint. */
object HorizontalBarSampleData {
    const val NAME: String = "Andres"
    const val ROLE: String = "Team Leader"

    val done: List<Float> = listOf(31f, 56f, 30f, 21f, 38f)
    val assigned: List<Float> = listOf(60f, 80f, 51f, 32f, 63f)

    /** New assigned counts of the same length. */
    fun randomAssigned(random: Random): List<Float> = List(assigned.size) { random.nextInt(25, 101).toFloat() }

    /** For each assigned count, a done count between 20% and 95% of it. */
    fun randomDone(assigned: List<Float>, random: Random): List<Float> =
        assigned.map { (it * random.nextInt(20, 96) / 100f) }
}
