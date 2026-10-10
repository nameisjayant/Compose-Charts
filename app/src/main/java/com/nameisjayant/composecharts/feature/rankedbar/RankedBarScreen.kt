package com.nameisjayant.composecharts.feature.rankedbar

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
import com.nameisjayant.charts.bar.RankedBar
import com.nameisjayant.charts.bar.RankedBarChartCard
import com.nameisjayant.charts.bar.RankedBarChartCardDefaults
import com.nameisjayant.charts.bar.RankedBarChartDefaults
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.composecharts.data.RankedBarSampleData
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme

@Composable
fun RankedBarRoute(
    onBack: () -> Unit,
    viewModel: RankedBarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RankedBarScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankedBarScreen(
    state: RankedBarState,
    onIntent: (RankedBarIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Ranked bar", style = MaterialTheme.typography.titleLarge) },
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
            val description = RankedBarChartCardDefaults.DescriptionTextStyle.copy(color = colors.onSurfaceVariant)
            RankedBarChartCard(
                title = "Average of the first economies",
                bars = RankedBarSampleData.countries.zip(state.scores) { country, score -> RankedBar(country, score) },
                description = "Economy score of the eight leading countries, highest first.",
                listTitle = "List of countries",
                listCaption = "${RankedBarSampleData.countries.size} countries",
                footer = "Scores are an index out of 100; shuffle to see the bars re-rank.",
                cardStyle = ChartCardDefaults.style(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = ChartCardDefaults.TitleTextStyle.copy(color = colors.onSurface),
                ),
                chartStyle = RankedBarChartDefaults.style(outsideLabelColor = colors.onSurface),
                contentStyle = RankedBarChartCardDefaults.style(
                    descriptionTextStyle = description,
                    listTitleTextStyle = RankedBarChartCardDefaults.ListTitleTextStyle.copy(color = colors.onSurface),
                    listCaptionTextStyle = RankedBarChartCardDefaults.ListCaptionTextStyle.copy(color = colors.onSurfaceVariant),
                    footerTextStyle = description,
                ),
                modifier = Modifier.fillMaxWidth().testTag(CHART_CARD_TAG),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(RankedBarIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(RankedBarIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RankedBarScreenPreview() {
    ComposeChartsTheme {
        RankedBarScreen(state = RankedBarState(), onIntent = {}, onBack = {})
    }
}
