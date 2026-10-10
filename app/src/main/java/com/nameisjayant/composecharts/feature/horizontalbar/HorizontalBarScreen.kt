package com.nameisjayant.composecharts.feature.horizontalbar

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
import com.nameisjayant.charts.bar.HorizontalBar
import com.nameisjayant.charts.bar.HorizontalBarChartCard
import com.nameisjayant.charts.bar.HorizontalBarChartCardDefaults
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartInitialsAvatar
import com.nameisjayant.charts.common.ChartPillButton
import com.nameisjayant.charts.core.mvi.CollectEffect
import com.nameisjayant.composecharts.data.HorizontalBarSampleData
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme

@Composable
fun HorizontalBarRoute(
    onBack: () -> Unit,
    viewModel: HorizontalBarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is HorizontalBarEffect.ShowProfile -> snackbarHostState.showSnackbar("Opening ${effect.name}'s profile")
        }
    }
    HorizontalBarScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorizontalBarScreen(
    state: HorizontalBarState,
    onIntent: (HorizontalBarIntent) -> Unit,
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
                title = { Text("Horizontal bar", style = MaterialTheme.typography.titleLarge) },
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
            HorizontalBarChartCard(
                title = "Horizontal bar chart with border radius",
                bars = state.done.zip(state.assigned) { done, assigned -> HorizontalBar(value = done, total = assigned) },
                name = HorizontalBarSampleData.NAME,
                role = HorizontalBarSampleData.ROLE,
                avatar = { ChartInitialsAvatar(text = HorizontalBarSampleData.NAME.take(1)) },
                action = {
                    ChartPillButton(
                        text = "PROFILE",
                        onClick = { onIntent(HorizontalBarIntent.OpenProfile) },
                        color = colors.onSurface,
                    )
                },
                description = "Tasks finished out of tasks assigned in each of the last five sprints, " +
                    "with the team's carry-over shown in the lighter track.",
                cardStyle = ChartCardDefaults.style(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = ChartCardDefaults.TitleTextStyle.copy(color = colors.onSurface),
                ),
                contentStyle = HorizontalBarChartCardDefaults.style(
                    nameTextStyle = HorizontalBarChartCardDefaults.NameTextStyle.copy(color = colors.onSurface),
                    roleTextStyle = HorizontalBarChartCardDefaults.RoleTextStyle.copy(color = colors.onSurfaceVariant),
                    descriptionTextStyle = HorizontalBarChartCardDefaults.DescriptionTextStyle.copy(color = colors.onSurface),
                ),
                modifier = Modifier.fillMaxWidth().testTag(CHART_CARD_TAG),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(HorizontalBarIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(HorizontalBarIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HorizontalBarScreenPreview() {
    ComposeChartsTheme {
        HorizontalBarScreen(state = HorizontalBarState(), onIntent = {}, onBack = {})
    }
}
