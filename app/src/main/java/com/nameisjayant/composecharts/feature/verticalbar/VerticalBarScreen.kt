package com.nameisjayant.composecharts.feature.verticalbar

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nameisjayant.charts.bar.BarSeries
import com.nameisjayant.charts.bar.VerticalBarChartCard
import com.nameisjayant.charts.bar.VerticalBarChartDefaults
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme

@Composable
fun VerticalBarRoute(
    onBack: () -> Unit,
    viewModel: VerticalBarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    VerticalBarScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalBarScreen(
    state: VerticalBarState,
    onIntent: (VerticalBarIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Vertical bar", style = MaterialTheme.typography.titleLarge) },
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
            VerticalBarChartCard(
                title = "Vertical bar",
                subtitle = "Statistics of the month",
                upperSeries = BarSeries(label = "Data one", values = state.upperValues, color = colors.primary),
                lowerSeries = BarSeries(label = "Data two", values = state.lowerValues, color = colors.secondary),
                cardStyle = ChartCardDefaults.style(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = ChartCardDefaults.TitleTextStyle.copy(color = colors.onSurface),
                    subtitleTextStyle = ChartCardDefaults.SubtitleTextStyle.copy(color = colors.onSurfaceVariant),
                ),
                legendStyle = ChartLegendDefaults.style(
                    textStyle = ChartLegendDefaults.TextStyle.copy(color = colors.onSurface),
                ),
                chartStyle = VerticalBarChartDefaults.style(
                    gridLineColor = colors.outlineVariant,
                    baselineColor = colors.outlineVariant,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(VerticalBarIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(VerticalBarIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VerticalBarScreenPreview() {
    ComposeChartsTheme {
        VerticalBarScreen(state = VerticalBarState(), onIntent = {}, onBack = {})
    }
}
