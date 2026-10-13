package com.nameisjayant.charts.heatmap

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

/** Visual configuration of a [HeatmapChart]. Build one with [HeatmapChartDefaults.style]. */
@Immutable
public data class HeatmapChartStyle(
    /** Largest a cell may get; cells shrink when the grid would not fit otherwise. */
    val cellSize: Dp,
    val cellCornerRadius: Dp,
    /** Smallest gap between two columns; columns are otherwise spread edge to edge. */
    val minColumnSpacing: Dp,
    val rowSpacing: Dp,
    val labelTextStyle: TextStyle,
    /** Gap between the column labels and the first row. */
    val labelSpacing: Dp,
)

public object HeatmapChartDefaults {
    public val EmptyColor: Color = Color(0xFFE6F5FC)
    public val LowColor: Color = Color(0xFFA5DEEE)
    public val MediumColor: Color = Color(0xFF3375B5)
    public val HighColor: Color = Color(0xFF03045E)

    /** From empty to busiest, the four shades of the design. */
    public val Colors: List<Color> = listOf(EmptyColor, LowColor, MediumColor, HighColor)

    public val LabelTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
    )

    public val AnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = 900, easing = FastOutSlowInEasing)

    public fun style(
        cellSize: Dp = 22.dp,
        cellCornerRadius: Dp = 4.dp,
        minColumnSpacing: Dp = 4.dp,
        rowSpacing: Dp = 14.dp,
        labelTextStyle: TextStyle = LabelTextStyle,
        labelSpacing: Dp = 20.dp,
    ): HeatmapChartStyle = HeatmapChartStyle(
        cellSize = cellSize,
        cellCornerRadius = cellCornerRadius,
        minColumnSpacing = minColumnSpacing,
        rowSpacing = rowSpacing,
        labelTextStyle = labelTextStyle,
        labelSpacing = labelSpacing,
    )
}

/**
 * A grid of rounded cells, one per value, each shaded by how big its value is, like a
 * contribution calendar. [values] holds the rows from top to bottom, each from left to right.
 *
 * A value of 0 or less takes the first of [colors]; the rest are split evenly between the
 * remaining colours, up to [maxValue] (the highest value by default). [columnLabels] are spread
 * evenly above the grid, the first over the first column and the last over the last.
 *
 * Unless the [modifier] sets a height, the chart is as tall as its rows at
 * [HeatmapChartStyle.cellSize] plus the labels.
 *
 * @param animationSpec how cells pop in, column by column, and change colour between data
 *   changes; `null` disables animation.
 */
@Composable
public fun HeatmapChart(
    values: List<List<Float>>,
    modifier: Modifier = Modifier,
    colors: List<Color> = HeatmapChartDefaults.Colors,
    columnLabels: List<String> = emptyList(),
    maxValue: Float? = null,
    style: HeatmapChartStyle = HeatmapChartDefaults.style(),
    animationSpec: AnimationSpec<Float>? = HeatmapChartDefaults.AnimationSpec,
) {
    val target = remember(values, maxValue, colors) { HeatmapGeometry.shades(values, maxValue, colors) }
    val currentAnimationSpec by rememberUpdatedState(animationSpec)

    // Cells pop in once; later data changes blend from whatever colours are on screen.
    val reveal = remember { Animatable(if (animationSpec == null) 1f else 0f) }
    val progress = remember { Animatable(1f) }
    var from by remember { mutableStateOf(target) }
    var to by remember { mutableStateOf(target) }

    LaunchedEffect(target) {
        if (target != to) {
            from = blend(from, to, progress.value)
            to = target
        }
        val spec = currentAnimationSpec
        if (spec == null) {
            reveal.snapTo(1f)
            progress.snapTo(1f)
        } else {
            if (reveal.value < 1f) launch { reveal.animateTo(1f, spec) }
            progress.snapTo(0f)
            progress.animateTo(1f, spec)
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val labels = remember(columnLabels, style.labelTextStyle) {
        columnLabels.map { textMeasurer.measure(it, style.labelTextStyle) }
    }

    val rows = values.size
    val labelRowHeight = labels.maxOfOrNull { it.size.height } ?: 0
    val height = with(LocalDensity.current) {
        val labelBlock = if (labels.isEmpty()) 0.dp else labelRowHeight.toDp() + style.labelSpacing
        labelBlock + style.cellSize * rows + style.rowSpacing * (rows - 1).coerceAtLeast(0)
    }

    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        drawHeatmap(
            shades = blend(from, to, progress.value),
            reveal = reveal.value,
            labels = labels,
            style = style,
        )
    }
}

private fun DrawScope.drawHeatmap(
    shades: List<List<Color>>,
    reveal: Float,
    labels: List<TextLayoutResult>,
    style: HeatmapChartStyle,
) {
    val rows = shades.size
    val columns = shades.maxOfOrNull { it.size } ?: 0
    val labelHeight = labels.maxOfOrNull { it.size.height } ?: 0
    val gridTop = if (labels.isEmpty()) 0f else labelHeight + style.labelSpacing.toPx()
    val rowSpacing = style.rowSpacing.toPx()
    val cell = HeatmapGeometry.cellSize(
        width = size.width,
        height = size.height - gridTop,
        columns = columns,
        rows = rows,
        maxCell = style.cellSize.toPx(),
        minColumnSpacing = style.minColumnSpacing.toPx(),
        rowSpacing = rowSpacing,
    )
    if (cell <= 0f) return

    labels.forEachIndexed { index, label ->
        val column = HeatmapGeometry.labelColumn(index, labels.size, columns)
        val x = HeatmapGeometry.left(column, columns, size.width, cell)
            .coerceAtMost(size.width - label.size.width)
            .coerceAtLeast(0f)
        drawText(label, topLeft = Offset(x, 0f))
    }

    val corner = style.cellCornerRadius.toPx()
    shades.forEachIndexed { row, rowShades ->
        val top = gridTop + row * (cell + rowSpacing)
        rowShades.forEachIndexed { column, color ->
            val scale = HeatmapGeometry.revealScale(reveal, column, columns)
            if (scale <= 0f) return@forEachIndexed
            val side = cell * scale
            val inset = (cell - side) / 2
            drawRoundRect(
                color = color,
                topLeft = Offset(HeatmapGeometry.left(column, columns, size.width, cell) + inset, top + inset),
                size = Size(side, side),
                cornerRadius = CornerRadius(min(corner * scale, side / 2)),
            )
        }
    }
}

/** The scale, shading and placement of a [HeatmapChart], kept apart from drawing so it can be tested. */
internal object HeatmapGeometry {
    /** Index into [levels] colours: 0 when empty, otherwise 1 up to the last by its share of [max]. */
    fun level(value: Float, max: Float, levels: Int): Int {
        if (levels <= 1 || value.isNaN() || value <= 0f || max <= 0f) return 0
        val buckets = levels - 1
        return ceil(min(value, max) / max * buckets).toInt().coerceIn(1, buckets)
    }

    /** The colour of every cell, with each row padded to the widest one using the empty colour. */
    fun shades(values: List<List<Float>>, maxValue: Float?, colors: List<Color>): List<List<Color>> {
        if (colors.isEmpty()) return values.map { row -> row.map { Color.Transparent } }
        val max = maxValue ?: values.maxOfOrNull { row -> row.filterNot { it.isNaN() }.maxOrNull() ?: 0f } ?: 0f
        val columns = values.maxOfOrNull { it.size } ?: 0
        return values.map { row ->
            List(columns) { column -> colors[level(row.getOrElse(column) { 0f }, max, colors.size)] }
        }
    }

    /** The largest square cell up to [maxCell] that fits [columns] by [rows] in [width] by [height]. */
    fun cellSize(
        width: Float,
        height: Float,
        columns: Int,
        rows: Int,
        maxCell: Float,
        minColumnSpacing: Float,
        rowSpacing: Float,
    ): Float {
        if (columns <= 0 || rows <= 0) return 0f
        val byWidth = (width - minColumnSpacing * (columns - 1)) / columns
        val byHeight = (height - rowSpacing * (rows - 1)) / rows
        return minOf(maxCell, byWidth, byHeight).coerceAtLeast(0f)
    }

    /** Left edge of column [index]: the first touches the left edge, the last the right; a lone column is centred. */
    fun left(index: Int, count: Int, width: Float, cell: Float): Float =
        if (count <= 1) (width - cell) / 2 else (width - cell) * index / (count - 1)

    /** The column label [index] of [labelCount] sits over, spread evenly from first to last. */
    fun labelColumn(index: Int, labelCount: Int, columns: Int): Int {
        if (labelCount <= 1 || columns <= 1) return 0
        return (index.toFloat() * (columns - 1) / (labelCount - 1)).roundToInt()
    }

    /** How grown a cell in [column] is at [reveal]: columns start one after another, left to right. */
    fun revealScale(reveal: Float, column: Int, columns: Int): Float {
        if (reveal >= 1f) return 1f
        val start = if (columns <= 1) 0f else column.toFloat() / (columns - 1) * STAGGER
        return ((reveal - start) / (1f - STAGGER)).coerceIn(0f, 1f)
    }

    /** Share of the reveal over which column starts are spread. */
    private const val STAGGER = 0.5f
}

/** [from] blended towards [to]; cells missing from [from] start at their [to] colour. */
private fun blend(from: List<List<Color>>, to: List<List<Color>>, fraction: Float): List<List<Color>> {
    if (fraction >= 1f) return to
    return to.mapIndexed { row, colors ->
        colors.mapIndexed { column, color ->
            lerp(from.getOrNull(row)?.getOrNull(column) ?: color, color, fraction)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HeatmapChartPreview() {
    HeatmapChart(
        values = listOf(
            listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
            listOf(0f, 7f, 0f, 12f, 14f, 0f, 0f, 3f, 0f, 0f, 0f),
            listOf(0f, 8f, 0f, 0f, 13f, 0f, 2f, 4f, 0f, 11f, 12f),
            listOf(0f, 13f, 0f, 0f, 3f, 14f, 0f, 0f, 4f, 2f, 0f),
            listOf(0f, 0f, 3f, 6f, 2f, 12f, 11f, 4f, 0f, 0f, 0f),
            listOf(0f, 4f, 0f, 2f, 0f, 0f, 13f, 14f, 0f, 0f, 0f),
        ),
        columnLabels = listOf("Nov", "Dec", "Jan", "Feb", "Mar", "Apr"),
        animationSpec = null,
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp),
    )
}
