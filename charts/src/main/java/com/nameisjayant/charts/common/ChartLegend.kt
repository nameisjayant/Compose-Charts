package com.nameisjayant.charts.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** One entry in a [ChartLegend]: a colour swatch followed by its [label]. */
@Immutable
public data class LegendItem(
    val label: String,
    val color: Color,
)

/** Visual configuration of a [ChartLegend]. Build one with [ChartLegendDefaults.style]. */
@Immutable
public data class ChartLegendStyle(
    val textStyle: TextStyle,
    val indicatorSize: DpSize,
    val indicatorShape: Shape,
    val indicatorTextSpacing: Dp,
    val itemSpacing: Dp,
    val lineSpacing: Dp,
)

public object ChartLegendDefaults {
    public val TextStyle: TextStyle = TextStyle(
        color = Color(0xFF111111),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
    )

    public fun style(
        textStyle: TextStyle = TextStyle,
        indicatorSize: DpSize = DpSize(width = 25.dp, height = 12.dp),
        indicatorShape: Shape = RoundedCornerShape(percent = 50),
        indicatorTextSpacing: Dp = 10.dp,
        itemSpacing: Dp = 24.dp,
        lineSpacing: Dp = 8.dp,
    ): ChartLegendStyle = ChartLegendStyle(
        textStyle = textStyle,
        indicatorSize = indicatorSize,
        indicatorShape = indicatorShape,
        indicatorTextSpacing = indicatorTextSpacing,
        itemSpacing = itemSpacing,
        lineSpacing = lineSpacing,
    )
}

/** Lays out [items] in a row, wrapping onto further lines when they don't fit. */
@Composable
public fun ChartLegend(
    items: List<LegendItem>,
    modifier: Modifier = Modifier,
    style: ChartLegendStyle = ChartLegendDefaults.style(),
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(style.itemSpacing),
        verticalArrangement = Arrangement.spacedBy(style.lineSpacing),
    ) {
        items.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(style.indicatorSize)
                        .background(color = item.color, shape = style.indicatorShape),
                )
                Spacer(Modifier.width(style.indicatorTextSpacing))
                BasicText(text = item.label, style = style.textStyle)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChartLegendPreview() {
    ChartLegend(
        items = listOf(
            LegendItem("Data one", Color(0xFF3375B5)),
            LegendItem("Data two", Color(0xFF52B2D6)),
        ),
    )
}
