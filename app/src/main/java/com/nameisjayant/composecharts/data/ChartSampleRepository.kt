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
        )
    }
}
