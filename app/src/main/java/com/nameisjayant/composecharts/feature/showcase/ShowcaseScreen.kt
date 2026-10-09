package com.nameisjayant.composecharts.feature.showcase

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nameisjayant.charts.core.mvi.CollectEffect
import com.nameisjayant.composecharts.data.ChartSample
import com.nameisjayant.composecharts.ui.theme.ComposeChartsTheme

private val TileShape = RoundedCornerShape(24.dp)
private val ThumbnailShape = RoundedCornerShape(18.dp)

@Composable
fun ShowcaseRoute(
    onOpenSample: (String) -> Unit,
    viewModel: ShowcaseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is ShowcaseEffect.NavigateToSample -> onOpenSample(effect.id)
        }
    }

    ShowcaseScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun ShowcaseScreen(
    state: ShowcaseState,
    onIntent: (ShowcaseIntent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        when {
            state.isLoading -> CenteredBox(innerPadding) { CircularProgressIndicator() }
            state.samples.isEmpty() -> CenteredBox(innerPadding) {
                Text(
                    text = "No charts yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = innerPadding.calculateTopPadding() + 24.dp,
                    bottom = innerPadding.calculateBottomPadding() + 24.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ShowcaseHeader(chartCount = state.samples.size)
                }
                items(state.samples, key = { it.id }) { sample ->
                    SampleTile(
                        sample = sample,
                        onClick = { onIntent(ShowcaseIntent.OpenSample(sample.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CenteredBox(innerPadding: PaddingValues, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun ShowcaseHeader(chartCount: Int) {
    Column(modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 12.dp)) {
        Text(
            text = "JETPACK COMPOSE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Compose Charts",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (chartCount == 1) "1 chart · tap a tile to explore" else "$chartCount charts · tap a tile to explore",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SampleTile(sample: ChartSample, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.96f else 1f, label = "tileScale")

    Column(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(TileShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), TileShape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
                .clip(ThumbnailShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 16.dp, vertical = 18.dp),
        ) {
            SampleThumbnail(sampleId = sample.id)
        }
        Column(modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 14.dp, bottom = 8.dp)) {
            Text(
                text = sample.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = sample.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            // Fixed two lines so tiles sharing a row line up at the same height.
            Text(
                text = sample.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowcaseScreenPreview() {
    ComposeChartsTheme {
        ShowcaseScreen(
            state = ShowcaseState(
                isLoading = false,
                samples = listOf(
                    ChartSample(
                        id = ChartSample.VERTICAL_BAR,
                        title = "Vertical bar",
                        description = "Two series compared above and below a shared baseline",
                        category = "Bar",
                    ),
                ),
            ),
            onIntent = {},
        )
    }
}
