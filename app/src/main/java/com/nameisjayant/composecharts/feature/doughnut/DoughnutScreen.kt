package com.nameisjayant.composecharts.feature.doughnut

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartDoughnutIcon
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.charts.donut.DonutSegment
import com.nameisjayant.charts.donut.DoughnutChartCard
import com.nameisjayant.charts.donut.DoughnutChartCardDefaults
import com.nameisjayant.charts.donut.DoughnutChartDefaults
import com.nameisjayant.composecharts.data.DoughnutSampleData
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme

@Composable
fun DoughnutRoute(
    onBack: () -> Unit,
    viewModel: DoughnutViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DoughnutScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoughnutScreen(
    state: DoughnutState,
    onIntent: (DoughnutIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val palette = doughnutPalette()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Doughnut", style = MaterialTheme.typography.titleLarge) },
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
            DoughnutChartCard(
                title = "Doughnut",
                subtitle = "Browser market shares at a specific website, 2014",
                segments = DoughnutSampleData.labels.mapIndexed { index, label ->
                    DonutSegment(label = label, value = state.shares[index], color = palette[index])
                },
                maxValue = DoughnutSampleData.TOTAL,
                icon = { ChartDoughnutIcon(color = colors.onSurface) },
                footer = { ProfileFooter(name = "Robert Fox", role = "Software Developer") },
                cardStyle = DoughnutChartCardDefaults.cardStyle(
                    subtitleTextStyle = DoughnutChartCardDefaults.SubtitleTextStyle.copy(
                        color = colors.onSurfaceVariant,
                    ),
                ).copy(
                    backgroundColor = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant),
                    titleTextStyle = ChartCardDefaults.TitleTextStyle.copy(color = colors.onSurface),
                ),
                legendStyle = ChartLegendDefaults.style(
                    textStyle = ChartLegendDefaults.TextStyle.copy(color = colors.onSurface),
                ),
                chartStyle = DoughnutChartDefaults.style(
                    trackColor = colors.surfaceVariant,
                    holeColor = colors.surface,
                    centerLabelTextStyle = DoughnutChartDefaults.CenterLabelTextStyle.copy(color = colors.onSurface),
                ),
                contentStyle = DoughnutChartCardDefaults.style(
                    headerBackgroundColor = colors.surfaceContainerLow,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { onIntent(DoughnutIntent.Shuffle) },
                    modifier = Modifier.weight(1f),
                ) { Text("Shuffle data") }
                OutlinedButton(
                    onClick = { onIntent(DoughnutIntent.Reset) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
            }
        }
    }
}

/** Who the data belongs to: an initials avatar, name and role, and a profile button. */
@Composable
private fun ProfileFooter(name: String, role: String) {
    val colors = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(colors.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                color = colors.onSecondaryContainer,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.titleLarge, color = colors.onSurface)
            Text(text = role, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        }
        // Profile is decorative in the sample; it just shows what the footer slot holds.
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = colors.inverseSurface, contentColor = colors.inverseOnSurface),
        ) { Text("PROFILE", fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp) }
    }
}

/** The library's doughnut colours, with the navy swapped for a lighter indigo on dark surfaces. */
@Composable
internal fun doughnutPalette(): List<Color> {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return listOf(
        if (isDark) Color(0xFF6E7BE0) else DoughnutChartDefaults.CssColor,
        DoughnutChartDefaults.HtmlColor,
        DoughnutChartDefaults.SassColor,
    )
}

@Preview(showBackground = true)
@Composable
private fun DoughnutScreenPreview() {
    ComposeChartsTheme {
        DoughnutScreen(state = DoughnutState(), onIntent = {}, onBack = {})
    }
}
