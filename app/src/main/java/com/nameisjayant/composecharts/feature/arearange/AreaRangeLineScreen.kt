package com.nameisjayant.composecharts.feature.arearange

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartFilterIcon
import com.nameisjayant.charts.line.AreaRangeLineChartCard
import com.nameisjayant.charts.line.AreaRangeLineChartDefaults
import com.nameisjayant.charts.line.AreaRangeLineSeries
import com.nameisjayant.composecharts.data.AreaRangeSampleData
import com.nameisjayant.composecharts.ui.CHART_CARD_TAG
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme
import kotlin.math.roundToInt

@Composable
fun AreaRangeLineRoute(
    onBack: () -> Unit,
    viewModel: AreaRangeLineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AreaRangeLineScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaRangeLineScreen(
    state: AreaRangeLineState,
    onIntent: (AreaRangeLineIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Area range and line", style = MaterialTheme.typography.titleLarge) },
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
            AreaRangeLineChartCard(
                title = "Temperatures",
                subtitle = "Average temperatures for July",
                icon = { ThermometerIcon(color = colors.onSurface) },
                action = { ChartFilterIcon(color = colors.onSurface) },
                series = AreaRangeLineSeries(
                    label = "Temperature",
                    points = state.points,
                    lineColor = colors.onSurface,
                    bandColor = colors.secondary,
                    pointColor = colors.primary,
                ),
                xRange = AreaRangeSampleData.temperatureRange,
                yRange = AreaRangeSampleData.altitudeRange,
                yLabelFormatter = { "${it.roundToInt()} km" },
                cardStyle = ChartCardDefaults.style(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = ChartCardDefaults.TitleTextStyle.copy(color = colors.onSurface),
                    subtitleTextStyle = ChartCardDefaults.SubtitleTextStyle.copy(color = colors.onSurfaceVariant),
                    titleSubtitleSpacing = 28.dp,
                    headerLegendSpacing = 24.dp,
                ),
                chartStyle = AreaRangeLineChartDefaults.style(
                    gridLineColor = colors.outlineVariant,
                    labelTextStyle = AreaRangeLineChartDefaults.LabelTextStyle.copy(color = colors.onSurface),
                ),
                modifier = Modifier.fillMaxWidth().testTag(CHART_CARD_TAG),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(AreaRangeLineIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(AreaRangeLineIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

/** Outlined thermometer: a rounded stem over a bulb, with a filled mercury column. */
@Composable
private fun ThermometerIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(width = 14.dp, height = 24.dp)) {
        val stroke = 2.dp.toPx()
        val bulbRadius = size.width / 2 - stroke / 2
        val bulbCenter = Offset(size.width / 2, size.height - bulbRadius - stroke / 2)
        val stemWidth = size.width * 0.5f
        val stemLeft = (size.width - stemWidth) / 2

        drawRoundRect(
            color = color,
            topLeft = Offset(stemLeft, stroke / 2),
            size = Size(stemWidth, bulbCenter.y - stroke / 2),
            cornerRadius = CornerRadius(stemWidth / 2),
            style = Stroke(width = stroke),
        )
        drawCircle(color = color, radius = bulbRadius, center = bulbCenter, style = Stroke(width = stroke))
        drawLine(
            color = color,
            start = Offset(size.width / 2, size.height * 0.35f),
            end = bulbCenter,
            strokeWidth = stroke,
        )
        drawCircle(color = color, radius = bulbRadius * 0.45f, center = bulbCenter)
    }
}

@Preview(showBackground = true)
@Composable
private fun AreaRangeLineScreenPreview() {
    ComposeChartsTheme {
        AreaRangeLineScreen(state = AreaRangeLineState(), onIntent = {}, onBack = {})
    }
}
