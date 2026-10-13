package com.nameisjayant.composecharts.feature.overlappingarea

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartChatIcon
import com.nameisjayant.charts.common.ChartCircleButton
import com.nameisjayant.charts.common.ChartInitialsAvatar
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.charts.core.mvi.CollectEffect
import com.nameisjayant.charts.line.AreaSeries
import com.nameisjayant.charts.line.OverlappingAreaChartCard
import com.nameisjayant.charts.line.OverlappingAreaChartCardDefaults
import com.nameisjayant.charts.line.OverlappingAreaChartDefaults
import com.nameisjayant.composecharts.data.OverlappingAreaSampleData
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme

@Composable
fun OverlappingAreaRoute(
    onBack: () -> Unit,
    viewModel: OverlappingAreaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is OverlappingAreaEffect.ShowChat -> snackbarHostState.showSnackbar("Messaging ${effect.name}")
        }
    }
    OverlappingAreaScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlappingAreaScreen(
    state: OverlappingAreaState,
    onIntent: (OverlappingAreaIntent) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Overlapping area", style = MaterialTheme.typography.titleLarge) },
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
            val description = OverlappingAreaChartCardDefaults.DescriptionTextStyle.copy(color = colors.onSurfaceVariant)
            OverlappingAreaChartCard(
                title = "Estimated Worldwide Population",
                series = listOf(
                    AreaSeries("2011", state.first, OverlappingAreaChartDefaults.FirstColor),
                    AreaSeries("2012", state.second, OverlappingAreaChartDefaults.SecondColor),
                ),
                filters = OverlappingAreaSampleData.regions,
                selectedFilter = state.region,
                onFilterSelected = { onIntent(OverlappingAreaIntent.SelectRegion(it)) },
                description = "Population index per month in ${OverlappingAreaSampleData.regions[state.region]}, " +
                    "2011 laid over 2012.",
                name = OverlappingAreaSampleData.NAME,
                role = OverlappingAreaSampleData.ROLE,
                avatar = { ChartInitialsAvatar(text = OverlappingAreaSampleData.NAME.take(1), size = 64.dp) },
                profileAction = {
                    ChartCircleButton(
                        onClick = { onIntent(OverlappingAreaIntent.OpenChat) },
                        backgroundColor = colors.surfaceVariant,
                    ) { ChartChatIcon(color = colors.onSurfaceVariant) }
                },
                footer = "Tap a region to compare its two years; shuffle to see the areas morph.",
                cardStyle = ChartCardDefaults.style(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = OverlappingAreaChartCardDefaults.cardStyle().titleTextStyle,
                ),
                legendStyle = OverlappingAreaChartCardDefaults.legendStyle().copy(
                    textStyle = ChartLegendDefaults.TextStyle.copy(color = colors.onSurface),
                ),
                contentStyle = OverlappingAreaChartCardDefaults.style(
                    filterTextStyle = OverlappingAreaChartCardDefaults.FilterTextStyle.copy(color = colors.onSurface),
                    filterBackgroundColor = colors.surfaceVariant,
                    descriptionTextStyle = description,
                    nameTextStyle = OverlappingAreaChartCardDefaults.NameTextStyle.copy(color = colors.onSurface),
                    roleTextStyle = OverlappingAreaChartCardDefaults.RoleTextStyle.copy(color = colors.onSurfaceVariant),
                    footerTextStyle = description,
                ),
                modifier = Modifier.fillMaxWidth().testTag(CHART_CARD_TAG),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(OverlappingAreaIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(OverlappingAreaIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OverlappingAreaScreenPreview() {
    ComposeChartsTheme {
        OverlappingAreaScreen(state = OverlappingAreaState(), onIntent = {}, onBack = {})
    }
}
