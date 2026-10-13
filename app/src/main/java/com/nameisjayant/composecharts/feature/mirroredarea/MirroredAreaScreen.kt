package com.nameisjayant.composecharts.feature.mirroredarea

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartOverflowIcon
import com.nameisjayant.charts.line.AreaSeries
import com.nameisjayant.charts.line.MirroredAreaChartCard
import com.nameisjayant.charts.line.MirroredAreaChartCardDefaults
import com.nameisjayant.charts.line.MirroredAreaChartDefaults
import com.nameisjayant.composecharts.data.MirroredAreaSampleData
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme

@Composable
fun MirroredAreaRoute(
    onBack: () -> Unit,
    viewModel: MirroredAreaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MirroredAreaScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MirroredAreaScreen(
    state: MirroredAreaState,
    onIntent: (MirroredAreaIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Mirrored area", style = MaterialTheme.typography.titleLarge) },
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
            MirroredAreaChartCard(
                title = "Intensity",
                subtitle = MirroredAreaSampleData.MONTH,
                description = "Time spent in each heart-rate zone, day by day across the month.",
                upperSeries = state.upper.mapIndexed { index, values ->
                    AreaSeries("Zone ${index + 1}", values, MirroredAreaChartDefaults.UpperColors[index])
                },
                lowerSeries = state.lower.mapIndexed { index, values ->
                    AreaSeries("Zone ${index + 1}", values, MirroredAreaChartDefaults.LowerColors[index])
                },
                summaryLabel = "Intensity zones",
                summaryValue = "${state.intensityPercent}%",
                action = { ChartOverflowIcon(color = colors.onSurface) },
                cardStyle = MirroredAreaChartCardDefaults.cardStyle(
                    titleTextStyle = MirroredAreaChartCardDefaults.cardStyle().titleTextStyle.copy(color = colors.onSurface),
                ).copy(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    subtitleTextStyle = ChartCardDefaults.SubtitleTextStyle.copy(color = colors.onSurfaceVariant),
                ),
                chartStyle = MirroredAreaChartDefaults.style(baselineColor = colors.surface.copy(alpha = 0.6f)),
                contentStyle = MirroredAreaChartCardDefaults.style(
                    descriptionTextStyle = MirroredAreaChartCardDefaults.DescriptionTextStyle.copy(color = colors.onSurfaceVariant),
                    summaryLabelTextStyle = MirroredAreaChartCardDefaults.SummaryLabelTextStyle.copy(color = colors.onSurface),
                    summaryValueTextStyle = MirroredAreaChartCardDefaults.SummaryValueTextStyle.copy(color = colors.onSurface),
                    swatchBorderColor = colors.surface,
                ),
                modifier = Modifier.fillMaxWidth().testTag(CHART_CARD_TAG),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(MirroredAreaIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(MirroredAreaIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MirroredAreaScreenPreview() {
    ComposeChartsTheme {
        MirroredAreaScreen(state = MirroredAreaState(), onIntent = {}, onBack = {})
    }
}
