package com.nameisjayant.composecharts.data

import com.nameisjayant.composecharts.di.DefaultDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/** Lists the charts the sample app demonstrates. Grows as charts are added to the library. */
@Singleton
class ChartSampleRepository @Inject constructor(
    @param:DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend fun samples(): List<ChartSample> = withContext(dispatcher) {
        listOf(
            ChartSample(
                id = ChartSample.VERTICAL_BAR,
                title = "Vertical bar",
                description = "Two series compared above and below a shared baseline",
                category = "Bar",
            ),
            ChartSample(
                id = ChartSample.AREA_RANGE_LINE,
                title = "Area range and line",
                description = "A smooth line wrapped in a soft ribbon, drawn top to bottom",
                category = "Line",
            ),
            ChartSample(
                id = ChartSample.DONUT,
                title = "Donut",
                description = "Rounded segments filling a share of a ring, with a summary below",
                category = "Pie",
            ),
            ChartSample(
                id = ChartSample.DOUGHNUT,
                title = "Doughnut",
                description = "Segments that thicken around a shaded hole, with a profile footer",
                category = "Pie",
            ),
            ChartSample(
                id = ChartSample.SPLINE_AREA,
                title = "Spline area",
                description = "A headline figure over a full-bleed trend line with a fading fill",
                category = "Line",
            ),
            ChartSample(
                id = ChartSample.STACKED_BAR,
                title = "Stacked bar",
                description = "Rounded stacks of three series under a title, description and trend",
                category = "Bar",
            ),
            ChartSample(
                id = ChartSample.HORIZONTAL_BAR,
                title = "Horizontal bar",
                description = "Pill-shaped progress bars under a profile row, with a short description",
                category = "Bar",
            ),
            ChartSample(
                id = ChartSample.RANKED_BAR,
                title = "Ranked bar",
                description = "A labelled staircase of bars, longest first, shading from navy to sky",
                category = "Bar",
            ),
            ChartSample(
                id = ChartSample.OVERLAPPING_AREA,
                title = "Overlapping area",
                description = "Two translucent years laid over each other, with region filters and a profile row",
                category = "Line",
            ),
            ChartSample(
                id = ChartSample.MIRRORED_AREA,
                title = "Mirrored area",
                description = "Smooth layered waves above and below a baseline, with a row of zone swatches",
                category = "Line",
            ),
            ChartSample(
                id = ChartSample.STACKED_COLUMN,
                title = "Stacked column",
                description = "Two series side by side in each column, with month tabs and a profile footer",
                category = "Bar",
            ),
            ChartSample(
                id = ChartSample.FLOATING_BAR,
                title = "Floating bar",
                description = "Bars spanning a low to a high value over a dashed grid, with time range chips",
                category = "Bar",
            ),
        )
    }
}
