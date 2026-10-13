package com.nameisjayant.composecharts.data

import kotlin.random.Random

/** Demo values for the floating bar sample: the low and high of each period, per time range. */
object FloatingBarSampleData {
    const val CAPTION: String = "Year 2021"
    const val DEFAULT_RANGE: Int = 2

    val ranges: List<String> = listOf("24h", "1d", "3d", "1w", "3w", "1m")

    /** Which palette colour each bar takes: 0 primary, 1 light, 2 accent. */
    val tones: List<Int> = listOf(0, 1, 0, 1, 2, 1, 0, 1, 0)

    /** Lowest value of each bar, one list per range. */
    val lows: List<List<Float>> = listOf(
        listOf(3600f, 2400f, 4100f, 3000f, 1200f, 3300f, 2000f, 2800f, 4400f),
        listOf(2200f, 3100f, 2600f, 4200f, 900f, 2700f, 3500f, 1900f, 3000f),
        listOf(2800f, 1800f, 3200f, 3800f, 500f, 2200f, 1700f, 2100f, 3100f),
        listOf(1500f, 2600f, 3800f, 2400f, 1100f, 3900f, 2300f, 3300f, 2000f),
        listOf(3100f, 1400f, 2200f, 3500f, 800f, 1800f, 2900f, 2500f, 3600f),
        listOf(2000f, 3300f, 1600f, 2800f, 600f, 2900f, 3800f, 1500f, 2400f),
    )

    /** Highest value of each bar, one list per range. */
    val highs: List<List<Float>> = listOf(
        listOf(5200f, 4300f, 6500f, 4600f, 6100f, 7200f, 4400f, 5000f, 6300f),
        listOf(4800f, 5000f, 5400f, 6100f, 5600f, 6600f, 5900f, 4100f, 4700f),
        listOf(5000f, 3000f, 6000f, 5100f, 5000f, 7800f, 4800f, 5700f, 4400f),
        listOf(3900f, 4700f, 6200f, 4100f, 5800f, 7400f, 5200f, 6100f, 3800f),
        listOf(5600f, 3700f, 5100f, 6300f, 4900f, 6800f, 4600f, 5300f, 5900f),
        listOf(4500f, 5800f, 4200f, 5600f, 5200f, 7600f, 6400f, 4000f, 5100f),
    )

    /** New lows and highs of the same length as each range's, every bar at least 1k tall. */
    fun randomBars(random: Random): Pair<List<Float>, List<Float>> {
        val lows = List(tones.size) { random.nextInt(3, 41).toFloat() * 100f }
        val highs = lows.map { it + random.nextInt(10, 41) * 100f }
        return lows to highs
    }
}
