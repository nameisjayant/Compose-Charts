package com.nameisjayant.composecharts.feature.heatmap

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
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCircleButton
import com.nameisjayant.charts.common.ChartSettingsIcon
import com.nameisjayant.charts.core.mvi.CollectEffect
import com.nameisjayant.charts.heatmap.HeatmapChartCard
import com.nameisjayant.charts.heatmap.HeatmapChartCardDefaults
import com.nameisjayant.charts.heatmap.HeatmapChartDefaults
import com.nameisjayant.composecharts.data.HeatmapSampleData
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme

@Composable
fun HeatmapRoute(
    onBack: () -> Unit,
    viewModel: HeatmapViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            HeatmapEffect.ShowSettings -> snackbarHostState.showSnackbar("Settings")
            HeatmapEffect.ShowActivity -> snackbarHostState.showSnackbar("Show more activity")
        }
    }
    HeatmapScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen(
    state: HeatmapState,
    onIntent: (HeatmapIntent) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = MaterialTheme.colorScheme
    // The design's black outlines and navy only work on a light surface; dark mode falls back to the theme.
    val isDark = colors.surface.luminance() < 0.5f
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Heatmap", style = MaterialTheme.typography.titleLarge) },
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
            val tab = HeatmapChartCardDefaults.TabTextStyle.copy(color = colors.onSurface)
            val legend = HeatmapChartCardDefaults.LegendTextStyle.copy(color = colors.onSurface)
            HeatmapChartCard(
                title = "Sales per employee per month",
                subtitle = "${state.total} contributions in ${HeatmapSampleData.years[state.year]}",
                values = state.values,
                action = {
                    ChartCircleButton(
                        onClick = { onIntent(HeatmapIntent.OpenSettings) },
                        backgroundColor = if (isDark) colors.surfaceVariant else Color(0xFFF2F2F2),
                    ) { ChartSettingsIcon(color = colors.onSurface) }
                },
                tabs = HeatmapSampleData.years,
                selectedTab = state.year,
                onTabSelected = { onIntent(HeatmapIntent.SelectYear(it)) },
                description = "Minim dolor in amet nulla laboris enim dolore consequatt.",
                columnLabels = HeatmapSampleData.columnLabels,
                colors = heatmapPalette(),
                note = "Learn how we count contributions",
                footerText = "Show more activity",
                onFooterClick = { onIntent(HeatmapIntent.ShowMoreActivity) },
                cardStyle = ChartCardDefaults.style(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = ChartCardDefaults.TitleTextStyle.copy(color = colors.onSurface),
                ),
                chartStyle = HeatmapChartDefaults.style(
                    labelTextStyle = HeatmapChartDefaults.LabelTextStyle.copy(color = colors.onSurface),
                ),
                contentStyle = HeatmapChartCardDefaults.style(
                    subtitleTextStyle = HeatmapChartCardDefaults.SubtitleTextStyle.copy(color = colors.onSurface),
                    descriptionTextStyle = HeatmapChartCardDefaults.DescriptionTextStyle.copy(color = colors.onSurfaceVariant),
                    tabTextStyle = tab,
                    selectedTabTextStyle = tab.copy(color = if (isDark) colors.onPrimary else Color.White),
                    tabBorderColor = colors.onSurface,
                    selectedTabBackgroundColor = if (isDark) colors.primary else HeatmapChartDefaults.MediumColor,
                    noteTextStyle = HeatmapChartCardDefaults.NoteTextStyle.copy(color = colors.onSurface),
                    legendTextStyle = legend,
                    footerTextStyle = HeatmapChartCardDefaults.FooterTextStyle.copy(color = colors.onSurface),
                    footerBackgroundColor = if (isDark) {
                        colors.surfaceVariant
                    } else {
                        HeatmapChartCardDefaults.FooterBackgroundColor
                    },
                ),
                modifier = Modifier.fillMaxWidth().testTag(CHART_CARD_TAG),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(HeatmapIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(HeatmapIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

/** The four cell shades, with the empty and navy ones swapped for tones that read on a dark surface. */
@Composable
internal fun heatmapPalette(): List<Color> {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.surface.luminance() < 0.5f
    return listOf(
        if (isDark) colors.surfaceVariant else HeatmapChartDefaults.EmptyColor,
        HeatmapChartDefaults.LowColor,
        HeatmapChartDefaults.MediumColor,
        if (isDark) Color(0xFF6E7BE0) else HeatmapChartDefaults.HighColor,
    )
}

@Preview(showBackground = true)
@Composable
private fun HeatmapScreenPreview() {
    ComposeChartsTheme {
        HeatmapScreen(state = HeatmapState(), onIntent = {}, onBack = {})
    }
}
