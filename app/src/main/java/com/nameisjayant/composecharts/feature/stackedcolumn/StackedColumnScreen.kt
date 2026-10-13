package com.nameisjayant.composecharts.feature.stackedcolumn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.nameisjayant.charts.bar.BarSeries
import com.nameisjayant.charts.bar.StackedColumnChartCard
import com.nameisjayant.charts.bar.StackedColumnChartCardDefaults
import com.nameisjayant.charts.bar.StackedColumnChartDefaults
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartChatIcon
import com.nameisjayant.charts.common.ChartCircleButton
import com.nameisjayant.charts.common.ChartInitialsAvatar
import com.nameisjayant.charts.core.mvi.CollectEffect
import com.nameisjayant.composecharts.data.StackedColumnSampleData
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme

@Composable
fun StackedColumnRoute(
    onBack: () -> Unit,
    viewModel: StackedColumnViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is StackedColumnEffect.ShowChat -> snackbarHostState.showSnackbar("Messaging ${effect.name}")
        }
    }
    StackedColumnScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackedColumnScreen(
    state: StackedColumnState,
    onIntent: (StackedColumnIntent) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = MaterialTheme.colorScheme
    val palette = stackedColumnPalette()
    // The design's cyan tints only work on a light surface; dark mode falls back to the theme.
    val isDark = colors.surface.luminance() < 0.5f
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Stacked column", style = MaterialTheme.typography.titleLarge) },
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
            val tab = StackedColumnChartCardDefaults.TabTextStyle.copy(color = colors.onSurface)
            StackedColumnChartCard(
                title = "Stacked column chart",
                series = listOf(
                    BarSeries("Apple", state.apples, palette[0]),
                    BarSeries("Banana", state.bananas, palette[1]),
                ),
                tabs = StackedColumnSampleData.months,
                selectedTab = state.month,
                onTabSelected = { onIntent(StackedColumnIntent.SelectMonth(it)) },
                description = "Apples and bananas sold each week of " +
                    "${StackedColumnSampleData.months[state.month]}, side by side.",
                name = StackedColumnSampleData.NAME,
                role = StackedColumnSampleData.ROLE,
                avatar = {
                    ChartInitialsAvatar(
                        text = StackedColumnSampleData.NAME.take(1),
                        modifier = Modifier.border(2.dp, colors.onSurface, CircleShape).padding(4.dp),
                    )
                },
                profileAction = {
                    ChartCircleButton(
                        onClick = { onIntent(StackedColumnIntent.OpenChat) },
                        backgroundColor = Color.Transparent,
                    ) { ChartChatIcon(color = colors.onSurface, size = 24.dp) }
                },
                cardStyle = ChartCardDefaults.style(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = StackedColumnChartCardDefaults.cardStyle().titleTextStyle.copy(color = colors.onSurface),
                ),
                legendStyle = StackedColumnChartCardDefaults.legendStyle().let {
                    it.copy(textStyle = it.textStyle.copy(color = colors.onSurface))
                },
                contentStyle = StackedColumnChartCardDefaults.style(
                    tabTextStyle = tab,
                    selectedTabTextStyle = if (isDark) tab.copy(color = colors.onSecondaryContainer) else tab,
                    selectedTabBackgroundColor = if (isDark) {
                        colors.secondaryContainer
                    } else {
                        StackedColumnChartCardDefaults.SelectedTabBackgroundColor
                    },
                    descriptionTextStyle = StackedColumnChartCardDefaults.DescriptionTextStyle.copy(color = colors.onSurfaceVariant),
                    footerBackgroundColor = if (isDark) {
                        colors.surfaceVariant.copy(alpha = 0.5f)
                    } else {
                        StackedColumnChartCardDefaults.FooterBackgroundColor
                    },
                    nameTextStyle = StackedColumnChartCardDefaults.NameTextStyle.copy(color = colors.onSurface),
                    roleTextStyle = StackedColumnChartCardDefaults.RoleTextStyle.copy(color = colors.onSurfaceVariant),
                ),
                modifier = Modifier.fillMaxWidth().testTag(CHART_CARD_TAG),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(StackedColumnIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(StackedColumnIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

/** The two series colours, with the navy lifted so it still reads on a dark surface. */
@Composable
internal fun stackedColumnPalette(): List<Color> {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return listOf(
        StackedColumnChartDefaults.FirstColor,
        if (isDark) Color(0xFF6E7BE0) else StackedColumnChartDefaults.SecondColor,
    )
}

@Preview(showBackground = true)
@Composable
private fun StackedColumnScreenPreview() {
    ComposeChartsTheme {
        StackedColumnScreen(state = StackedColumnState(), onIntent = {}, onBack = {})
    }
}
