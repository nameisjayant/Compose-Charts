package com.nameisjayant.composecharts.feature.floatingbar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nameisjayant.charts.bar.FloatingBar
import com.nameisjayant.charts.bar.FloatingBarChartCard
import com.nameisjayant.charts.bar.FloatingBarChartCardDefaults
import com.nameisjayant.charts.bar.FloatingBarChartDefaults
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCircleButton
import com.nameisjayant.charts.common.ChartSortIcon
import com.nameisjayant.charts.core.mvi.CollectEffect
import com.nameisjayant.composecharts.data.FloatingBarSampleData
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme

@Composable
fun FloatingBarRoute(
    onBack: () -> Unit,
    viewModel: FloatingBarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            FloatingBarEffect.ShowFilters -> snackbarHostState.showSnackbar("Filters")
        }
    }
    FloatingBarScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingBarScreen(
    state: FloatingBarState,
    onIntent: (FloatingBarIntent) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = MaterialTheme.colorScheme
    // The design's navy only works on a light surface; dark mode falls back to the theme.
    val isDark = colors.surface.luminance() < 0.5f
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Floating bar", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text(text = "←", fontSize = 22.sp) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            val chip = FloatingBarChartCardDefaults.ChipTextStyle.copy(color = colors.onSurface)
            FloatingBarChartCard(
                title = "Statistic",
                description = "Lowest and highest daily values for each period of the " +
                    "${FloatingBarSampleData.ranges[state.range]} range.",
                bars = floatingBars(state.lows, state.highs),
                action = {
                    ChartCircleButton(
                        onClick = { onIntent(FloatingBarIntent.OpenFilters) },
                        backgroundColor = if (isDark) colors.surfaceVariant else Color(0xFFF2F2F2),
                    ) { ChartSortIcon(color = colors.onSurface) }
                },
                caption = FloatingBarSampleData.CAPTION,
                ranges = FloatingBarSampleData.ranges,
                selectedRange = state.range,
                onRangeSelected = { onIntent(FloatingBarIntent.SelectRange(it)) },
                cardStyle = ChartCardDefaults.style(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = ChartCardDefaults.TitleTextStyle.copy(color = colors.onSurface),
                ),
                chartStyle = FloatingBarChartDefaults.style(
                    gridLineColor = colors.onSurface.copy(alpha = 0.7f),
                    labelTextStyle = FloatingBarChartDefaults.LabelTextStyle.copy(color = colors.onSurface),
                ),
                contentStyle = FloatingBarChartCardDefaults.style(
                    descriptionTextStyle = FloatingBarChartCardDefaults.DescriptionTextStyle.copy(color = colors.onSurfaceVariant),
                    captionTextStyle = FloatingBarChartCardDefaults.CaptionTextStyle.copy(color = colors.onSurfaceVariant),
                    chipTextStyle = chip,
                    selectedChipTextStyle = chip.copy(color = if (isDark) colors.onPrimary else Color.White),
                    chipBackgroundColor = if (isDark) {
                        colors.surfaceVariant
                    } else {
                        FloatingBarChartCardDefaults.ChipBackgroundColor
                    },
                    selectedChipBackgroundColor = if (isDark) colors.primary else FloatingBarChartDefaults.AccentColor,
                ),
                modifier = Modifier.fillMaxWidth().testTag(CHART_CARD_TAG),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(FloatingBarIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(FloatingBarIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

/** Pairs each low with its high, coloured by [FloatingBarSampleData.tones]. */
@Composable
internal fun floatingBars(lows: List<Float>, highs: List<Float>): List<FloatingBar> {
    val palette = floatingBarPalette()
    return lows.zip(highs).mapIndexed { index, (low, high) ->
        FloatingBar(low, high, palette[FloatingBarSampleData.tones.getOrElse(index) { 0 }])
    }
}

/** The three bar colours, with the navy lifted so it still reads on a dark surface. */
@Composable
internal fun floatingBarPalette(): List<Color> {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return listOf(
        FloatingBarChartDefaults.PrimaryColor,
        FloatingBarChartDefaults.LightColor,
        if (isDark) Color(0xFF6E7BE0) else FloatingBarChartDefaults.AccentColor,
    )
}

@Preview(showBackground = true)
@Composable
private fun FloatingBarScreenPreview() {
    ComposeChartsTheme {
        FloatingBarScreen(state = FloatingBarState(), onIntent = {}, onBack = {})
    }
}
