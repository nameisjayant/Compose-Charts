package com.nameisjayant.composecharts.feature.donut

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.charts.common.ChartSettingsIcon
import com.nameisjayant.charts.donut.DonutChartCard
import com.nameisjayant.charts.donut.DonutChartCardDefaults
import com.nameisjayant.charts.donut.DonutChartDefaults
import com.nameisjayant.charts.donut.DonutSegment
import com.nameisjayant.composecharts.data.DonutSampleData
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme
import kotlin.math.roundToInt

@Composable
fun DonutRoute(
    onBack: () -> Unit,
    viewModel: DonutViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DonutScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonutScreen(
    state: DonutState,
    onIntent: (DonutIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val palette = donutPalette()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Donut", style = MaterialTheme.typography.titleLarge) },
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
            DonutChartCard(
                title = "Storage of your device",
                segments = DonutSampleData.labels.mapIndexed { index, label ->
                    DonutSegment(label = label, value = state.usedGb[index], color = palette[index])
                },
                maxValue = DonutSampleData.CAPACITY_GB,
                caption = "Used storage",
                value = "${state.totalUsedGb.roundToInt()}GB",
                action = {
                    // Settings is decorative in the sample; it just shows what the action slot holds.
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(colors.surfaceVariant, CircleShape)
                            .clickable(role = Role.Button, onClickLabel = "Settings") {},
                        contentAlignment = Alignment.Center,
                    ) { ChartSettingsIcon(color = colors.onSurface) }
                },
                cardStyle = DonutChartCardDefaults.cardStyle(
                    captionTextStyle = DonutChartCardDefaults.CaptionTextStyle.copy(color = colors.onSurface),
                ).copy(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = ChartCardDefaults.TitleTextStyle.copy(color = colors.onSurface),
                ),
                legendStyle = DonutChartCardDefaults.legendStyle(
                    textStyle = ChartLegendDefaults.TextStyle.copy(
                        color = colors.onSurface,
                        fontSize = 17.sp,
                        fontWeight = null,
                    ),
                ),
                chartStyle = DonutChartDefaults.style(
                    trackColor = colors.surfaceVariant,
                    centerLabelTextStyle = DonutChartDefaults.CenterLabelTextStyle.copy(
                        color = colors.onSurfaceVariant.copy(alpha = 0.6f),
                    ),
                ),
                contentStyle = DonutChartCardDefaults.style(
                    valueTextStyle = DonutChartCardDefaults.ValueTextStyle.copy(color = colors.onSurface),
                ),
                modifier = Modifier.fillMaxWidth().testTag(CHART_CARD_TAG),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(DonutIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(DonutIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

/** The library's storage colours, with the navy swapped for a lighter indigo on dark surfaces. */
@Composable
internal fun donutPalette(): List<Color> {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return listOf(
        if (isDark) Color(0xFF6E7BE0) else DonutChartDefaults.DocumentsColor,
        DonutChartDefaults.PhotosColor,
        DonutChartDefaults.MusicColor,
        DonutChartDefaults.VideosColor,
    )
}

@Preview(showBackground = true)
@Composable
private fun DonutScreenPreview() {
    ComposeChartsTheme {
        DonutScreen(state = DonutState(), onIntent = {}, onBack = {})
    }
}
