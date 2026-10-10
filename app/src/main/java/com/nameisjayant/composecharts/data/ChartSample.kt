package com.nameisjayant.composecharts.data

/** One entry in the sample app's catalogue; each chart the library ships gets one. */
data class ChartSample(
    val id: String,
    val title: String,
    val description: String,
    /** Chart family shown as a small overline on the tile, e.g. "Bar". */
    val category: String,
) {
    companion object {
        const val VERTICAL_BAR = "vertical_bar"
        const val AREA_RANGE_LINE = "area_range_line"
        const val DONUT = "donut"
    }
}
