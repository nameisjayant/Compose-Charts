package com.nameisjayant.composecharts.feature.splinearea

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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nameisjayant.charts.common.ChartBarsIcon
import com.nameisjayant.charts.line.SplineAreaChartCard
import com.nameisjayant.charts.line.SplineAreaChartCardDefaults
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme
import java.util.Locale

@Composable
fun SplineAreaRoute(
    onBack: () -> Unit,
    viewModel: SplineAreaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SplineAreaScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplineAreaScreen(
    state: SplineAreaState,
    onIntent: (SplineAreaIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.surface.luminance() < 0.5f
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Spline area", style = MaterialTheme.typography.titleLarge) },
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
            SplineAreaChartCard(
                title = "Expected earnings",
                value = String.format(Locale.US, "€%.1f", state.earnings),
                badge = String.format(Locale.US, "%+.2f%%", state.changePercent),
                values = state.trend,
                action = {
                    ChartBarsIcon(color = if (isDark) colors.primary else SplineAreaChartCardDefaults.ActionIconColor)
                },
                cardStyle = SplineAreaChartCardDefaults.cardStyle(
                    titleTextStyle = SplineAreaChartCardDefaults.TitleTextStyle.copy(color = colors.onSurface),
                ).copy(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                ),
                contentStyle = SplineAreaChartCardDefaults.style(
                    valueTextStyle = SplineAreaChartCardDefaults.ValueTextStyle.copy(color = colors.onSurface),
                    // A falling figure gets a warning-coloured badge.
                    badgeColor = if (state.changePercent < 0f) colors.error else SplineAreaChartCardDefaults.BadgeColor,
                    badgeTextStyle = SplineAreaChartCardDefaults.BadgeTextStyle.copy(
                        color = if (state.changePercent < 0f) colors.onError else SplineAreaChartCardDefaults.BadgeTextStyle.color,
                    ),
                    actionBackgroundColor = if (isDark) colors.surfaceVariant else SplineAreaChartCardDefaults.ActionBackgroundColor,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(SplineAreaIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(SplineAreaIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplineAreaScreenPreview() {
    ComposeChartsTheme {
        SplineAreaScreen(state = SplineAreaState(), onIntent = {}, onBack = {})
    }
}
