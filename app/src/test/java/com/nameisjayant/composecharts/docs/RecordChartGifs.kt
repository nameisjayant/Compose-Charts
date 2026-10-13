package com.nameisjayant.composecharts.docs

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.nameisjayant.composecharts.feature.arearange.AreaRangeLineScreen
import com.nameisjayant.composecharts.feature.arearange.AreaRangeLineViewModel
import com.nameisjayant.composecharts.feature.donut.DonutScreen
import com.nameisjayant.composecharts.feature.donut.DonutViewModel
import com.nameisjayant.composecharts.feature.doughnut.DoughnutScreen
import com.nameisjayant.composecharts.feature.doughnut.DoughnutViewModel
import com.nameisjayant.composecharts.feature.horizontalbar.HorizontalBarScreen
import com.nameisjayant.composecharts.feature.horizontalbar.HorizontalBarViewModel
import com.nameisjayant.composecharts.feature.overlappingarea.OverlappingAreaScreen
import com.nameisjayant.composecharts.feature.overlappingarea.OverlappingAreaViewModel
import com.nameisjayant.composecharts.feature.rankedbar.RankedBarScreen
import com.nameisjayant.composecharts.feature.rankedbar.RankedBarViewModel
import com.nameisjayant.composecharts.feature.splinearea.SplineAreaScreen
import com.nameisjayant.composecharts.feature.splinearea.SplineAreaViewModel
import com.nameisjayant.composecharts.feature.stackedbar.StackedBarScreen
import com.nameisjayant.composecharts.feature.stackedbar.StackedBarViewModel
import com.nameisjayant.composecharts.feature.verticalbar.VerticalBarScreen
import com.nameisjayant.composecharts.feature.verticalbar.VerticalBarViewModel
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme
import java.io.File
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Records the frames behind the README GIFs: each sample screen is rendered off-device, and only
 * its chart card is captured while the chart animates in, shuffles twice and resets.
 *
 * Skipped unless `-PchartGifDir=<dir>` is passed; run `scripts/record-chart-gifs.sh` instead.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w411dp-h891dp-xxhdpi")
class RecordChartGifs {

    @get:Rule
    val composeRule = createComposeRule()

    private val outputDir: File? = System.getProperty("chartGifDir")?.let(::File)

    @Test
    fun verticalBar() {
        val viewModel = VerticalBarViewModel()
        record("vertical-bar", shuffle = "Shuffle data") {
            val state by viewModel.state.collectAsState()
            VerticalBarScreen(state = state, onIntent = viewModel::onIntent, onBack = {})
        }
    }

    @Test
    fun areaRangeLine() {
        val viewModel = AreaRangeLineViewModel()
        record("area-range-line", shuffle = "Shuffle data") {
            val state by viewModel.state.collectAsState()
            AreaRangeLineScreen(state = state, onIntent = viewModel::onIntent, onBack = {})
        }
    }

    @Test
    fun donut() {
        val viewModel = DonutViewModel()
        record("donut", shuffle = "Shuffle data") {
            val state by viewModel.state.collectAsState()
            DonutScreen(state = state, onIntent = viewModel::onIntent, onBack = {})
        }
    }

    @Test
    fun doughnut() {
        val viewModel = DoughnutViewModel()
        record("doughnut", shuffle = "Shuffle data") {
            val state by viewModel.state.collectAsState()
            DoughnutScreen(state = state, onIntent = viewModel::onIntent, onBack = {})
        }
    }

    @Test
    fun splineArea() {
        val viewModel = SplineAreaViewModel()
        record("spline-area", shuffle = "Shuffle data") {
            val state by viewModel.state.collectAsState()
            SplineAreaScreen(state = state, onIntent = viewModel::onIntent, onBack = {})
        }
    }

    @Test
    fun stackedBar() {
        val viewModel = StackedBarViewModel()
        record("stacked-bar", shuffle = "Shuffle data") {
            val state by viewModel.state.collectAsState()
            StackedBarScreen(state = state, onIntent = viewModel::onIntent, onBack = {})
        }
    }

    @Test
    fun horizontalBar() {
        val viewModel = HorizontalBarViewModel()
        record("horizontal-bar", shuffle = "Shuffle data") {
            val state by viewModel.state.collectAsState()
            HorizontalBarScreen(state = state, onIntent = viewModel::onIntent, onBack = {})
        }
    }

    @Test
    fun rankedBar() {
        val viewModel = RankedBarViewModel()
        record("ranked-bar", shuffle = "Shuffle data") {
            val state by viewModel.state.collectAsState()
            RankedBarScreen(state = state, onIntent = viewModel::onIntent, onBack = {})
        }
    }

    @Test
    fun overlappingArea() {
        val viewModel = OverlappingAreaViewModel()
        record("overlapping-area", shuffle = "Shuffle data") {
            val state by viewModel.state.collectAsState()
            OverlappingAreaScreen(state = state, onIntent = viewModel::onIntent, onBack = {})
        }
    }

    private fun record(name: String, shuffle: String, content: @Composable () -> Unit) {
        assumeTrue("Pass -PchartGifDir to record GIF frames", outputDir != null)
        val frameDir = File(outputDir, name).apply {
            deleteRecursively()
            mkdirs()
        }
        var frame = 0
        fun capture(durationMillis: Long) {
            repeat((durationMillis / FRAME_MILLIS).toInt()) {
                val bitmap = composeRule.onNodeWithTag(CHART_CARD_TAG).captureToImage().asAndroidBitmap()
                File(frameDir, "frame_%04d.png".format(frame++)).outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                composeRule.mainClock.advanceTimeBy(FRAME_MILLIS)
            }
        }

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { ComposeChartsTheme(darkTheme = false, content = content) }
        capture(INTRO_MILLIS)
        repeat(2) {
            composeRule.onNodeWithText(shuffle).performClick()
            capture(STEP_MILLIS)
        }
        composeRule.onNodeWithText("Reset").performClick()
        capture(STEP_MILLIS)
    }

    private companion object {
        const val FRAME_MILLIS = 40L
        const val INTRO_MILLIS = 2_400L
        const val STEP_MILLIS = 2_000L
    }
}
