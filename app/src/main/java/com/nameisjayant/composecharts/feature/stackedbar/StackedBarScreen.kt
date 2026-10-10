package com.nameisjayant.composecharts.feature.stackedbar

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nameisjayant.charts.bar.BarSeries
import com.nameisjayant.charts.bar.StackedBarChartCard
import com.nameisjayant.charts.bar.StackedBarChartCardDefaults
import com.nameisjayant.charts.bar.StackedBarChartDefaults
import com.nameisjayant.charts.common.ChartBarsIcon
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme
import java.util.Locale

@Composable
fun StackedBarRoute(
    onBack: () -> Unit,
    viewModel: StackedBarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StackedBarScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackedBarScreen(
    state: StackedBarState,
    onIntent: (StackedBarIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val palette = stackedBarPalette()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Stacked bar", style = MaterialTheme.typography.titleLarge) },
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
            StackedBarChartCard(
                title = "Bar chart",
                subtitle = "Orders per day, split by channel: store, website and mobile app.",
                trend = String.format(Locale.US, "%+d%%", state.changePercent),
                series = listOf(
                    BarSeries(label = "Store", values = state.store, color = palette[0]),
                    BarSeries(label = "Web", values = state.web, color = palette[1]),
                    BarSeries(label = "App", values = state.app, color = palette[2]),
                ),
                icon = { ChartBarsIcon(color = colors.onSurface, barHeights = listOf(0.45f, 0.7f, 1f)) },
                cardStyle = StackedBarChartCardDefaults.cardStyle(
                    subtitleTextStyle = StackedBarChartCardDefaults.SubtitleTextStyle.copy(color = colors.onSurfaceVariant),
                ).copy(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = ChartCardDefaults.TitleTextStyle.copy(color = colors.onSurface),
                ),
                contentStyle = StackedBarChartCardDefaults.style(
                    trendTextStyle = StackedBarChartCardDefaults.TrendTextStyle.copy(
                        // A falling figure is called out in the warning colour.
                        color = if (state.changePercent < 0) colors.error else colors.onSurface,
                    ),
                ),
                modifier = Modifier.fillMaxWidth().testTag(CHART_CARD_TAG),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(StackedBarIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(StackedBarIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

/** Bottom-to-top segment colours; the navy base is lifted on dark surfaces so it stays visible. */
@Composable
internal fun stackedBarPalette(): List<Color> {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return listOf(
        if (isDark) Color(0xFF6E7BE0) else StackedBarChartDefaults.BottomColor,
        StackedBarChartDefaults.MiddleColor,
        StackedBarChartDefaults.TopColor,
    )
}

@Preview(showBackground = true)
@Composable
private fun StackedBarScreenPreview() {
    ComposeChartsTheme {
        StackedBarScreen(state = StackedBarState(), onIntent = {}, onBack = {})
    }
}
