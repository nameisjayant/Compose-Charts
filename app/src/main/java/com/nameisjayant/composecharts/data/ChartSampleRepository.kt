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
        )
    }
}
