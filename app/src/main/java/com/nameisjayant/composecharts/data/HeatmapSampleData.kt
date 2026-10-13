package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the heatmap sample: six rows of eleven columns per year. */
object HeatmapSampleData {
    const val DEFAULT_YEAR: Int = 0
    private const val ROWS: Int = 6
    private const val COLUMNS: Int = 11

    val years: List<String> = listOf("2016", "2017", "2018", "2019")

    val columnLabels: List<String> = listOf("Nov", "Dec", "Jan", "Feb", "Mar", "Apr")

    /** Contributions per cell, one grid of rows (top to bottom) per year. */
    val values: List<List<List<Float>>> = listOf(
        grid(
            "0 0 0 0 0 0 0 0 0 0 0",
            "0 7 0 12 14 0 0 3 0 0 0",
            "0 8 0 0 13 0 2 4 0 11 12",
            "0 13 0 0 3 14 0 0 4 2 0",
            "0 0 3 6 2 12 11 4 0 0 0",
            "0 4 0 2 0 0 13 14 0 0 0",
        ),
        grid(
            "2 0 0 6 0 0 3 0 12 0 0",
            "0 4 0 0 13 0 0 7 0 0 2",
            "0 0 11 0 0 3 0 0 0 14 0",
            "5 0 0 2 0 0 12 0 4 0 0",
            "0 0 0 0 8 0 0 0 0 3 0",
            "0 12 0 0 0 4 0 13 0 0 6",
        ),
        grid(
            "0 0 3 0 0 0 0 0 4 0 0",
            "0 2 0 0 7 0 0 0 0 0 3",
            "0 0 0 12 0 0 4 0 0 0 0",
            "3 0 0 0 0 14 0 0 8 0 0",
            "0 0 6 0 0 0 0 2 0 0 13",
            "0 0 0 0 3 0 0 0 0 4 0",
        ),
        grid(
            "4 8 0 2 13 0 6 0 3 12 0",
            "0 12 3 0 7 4 0 14 0 0 2",
            "6 0 13 4 0 11 3 0 8 4 0",
            "0 3 0 12 2 0 14 6 0 13 7",
            "12 0 4 0 6 13 0 3 11 0 4",
            "0 7 0 3 0 2 8 0 4 6 14",
        ),
    )

    /** A new grid of the same shape, with about half the cells empty. */
    fun randomValues(random: Random): List<List<Float>> = List(ROWS) {
        List(COLUMNS) { if (random.nextFloat() < 0.5f) 0f else random.nextInt(1, 15).toFloat() }
    }

    private fun grid(vararg rows: String): List<List<Float>> = rows.map { row -> row.split(" ").map(String::toFloat) }
}
