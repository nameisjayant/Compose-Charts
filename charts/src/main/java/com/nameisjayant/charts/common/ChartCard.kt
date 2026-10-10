package com.nameisjayant.charts.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Visual configuration of a [ChartCard]. Build one with [ChartCardDefaults.style]. */
@Immutable
public data class ChartCardStyle(
    val backgroundColor: Color,
    val shape: Shape,
    val border: BorderStroke?,
    val elevation: Dp,
    val contentPadding: PaddingValues,
    val titleTextStyle: TextStyle,
    val subtitleTextStyle: TextStyle,
    val iconTitleSpacing: Dp,
    val titleSubtitleSpacing: Dp,
    val headerLegendSpacing: Dp,
    val legendContentSpacing: Dp,
)

public object ChartCardDefaults {
    public val TitleTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
    )

    public val SubtitleTextStyle: TextStyle = TextStyle(
        color = Color(0xFF757575),
        fontSize = 15.sp,
    )

    public fun style(
        backgroundColor: Color = Color.White,
        shape: Shape = RoundedCornerShape(24.dp),
        border: BorderStroke? = null,
        elevation: Dp = 0.dp,
        contentPadding: PaddingValues = PaddingValues(24.dp),
        titleTextStyle: TextStyle = TitleTextStyle,
        subtitleTextStyle: TextStyle = SubtitleTextStyle,
        iconTitleSpacing: Dp = 8.dp,
        titleSubtitleSpacing: Dp = 2.dp,
        headerLegendSpacing: Dp = 36.dp,
        legendContentSpacing: Dp = 36.dp,
    ): ChartCardStyle = ChartCardStyle(
        backgroundColor = backgroundColor,
        shape = shape,
        border = border,
        elevation = elevation,
        contentPadding = contentPadding,
        titleTextStyle = titleTextStyle,
        subtitleTextStyle = subtitleTextStyle,
        iconTitleSpacing = iconTitleSpacing,
        titleSubtitleSpacing = titleSubtitleSpacing,
        headerLegendSpacing = headerLegendSpacing,
        legendContentSpacing = legendContentSpacing,
    )
}

/**
 * A card that frames a chart: [title] with an optional leading [icon] and trailing [action]
 * (e.g. an overflow menu), an optional [subtitle], then an optional [legend], then the chart
 * [content].
 */
@Composable
public fun ChartCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    legend: (@Composable () -> Unit)? = null,
    style: ChartCardStyle = ChartCardDefaults.style(),
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.chartCardFrame(style)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                icon()
                Spacer(Modifier.width(style.iconTitleSpacing))
            }
            BasicText(text = title, style = style.titleTextStyle, modifier = Modifier.weight(1f))
            action?.invoke()
        }
        if (subtitle != null) {
            Spacer(Modifier.height(style.titleSubtitleSpacing))
            BasicText(text = subtitle, style = style.subtitleTextStyle)
        }
        Spacer(Modifier.height(style.headerLegendSpacing))
        if (legend != null) {
            legend()
            Spacer(Modifier.height(style.legendContentSpacing))
        }
        content()
    }
}

/** The background, border, elevation and padding of a [ChartCard], for cards with their own layout. */
internal fun Modifier.chartCardFrame(style: ChartCardStyle): Modifier = this
    .shadow(elevation = style.elevation, shape = style.shape)
    .clip(style.shape)
    .background(color = style.backgroundColor, shape = style.shape)
    .then(
        if (style.border != null) Modifier.border(style.border, style.shape) else Modifier,
    )
    .padding(style.contentPadding)

/** Three vertically stacked dots, the usual "more options" glyph for a [ChartCard] action. */
@Composable
public fun ChartOverflowIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF000000),
    dotSize: Dp = 4.dp,
    dotSpacing: Dp = 4.dp,
) {
    Canvas(modifier = modifier.size(DpSize(width = dotSize, height = dotSize * 3 + dotSpacing * 2))) {
        val radius = dotSize.toPx() / 2
        val step = dotSize.toPx() + dotSpacing.toPx()
        repeat(3) { index ->
            drawCircle(color = color, radius = radius, center = Offset(radius, radius + step * index))
        }
    }
}

/** Two stacked bars, the lower one shorter and right-aligned: a compact "filter" glyph. */
@Composable
public fun ChartFilterIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF000000),
    width: Dp = 18.dp,
    barThickness: Dp = 3.dp,
    barSpacing: Dp = 3.dp,
    /** Length of the lower bar as a fraction of [width]. */
    lowerBarFraction: Float = 0.6f,
) {
    Canvas(modifier = modifier.size(DpSize(width = width, height = barThickness * 2 + barSpacing))) {
        val thickness = barThickness.toPx()
        val half = thickness / 2
        val upperY = half
        val lowerY = size.height - half
        drawLine(color, Offset(half, upperY), Offset(size.width - half, upperY), thickness, StrokeCap.Round)
        val lowerStart = size.width * (1f - lowerBarFraction.coerceIn(0f, 1f)) + half
        drawLine(color, Offset(lowerStart, lowerY), Offset(size.width - half, lowerY), thickness, StrokeCap.Round)
    }
}

/** A cog wheel with [teeth] teeth around a punched-out hub: a "settings" glyph. */
@Composable
public fun ChartSettingsIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF000000),
    size: Dp = 22.dp,
    teeth: Int = 8,
) {
    Spacer(
        modifier = modifier
            .size(size)
            .drawWithCache {
                val gear = gearPath(
                    center = Offset(this.size.width / 2, this.size.height / 2),
                    outerRadius = this.size.minDimension / 2,
                    teeth = teeth,
                )
                onDrawBehind { drawPath(gear, color) }
            },
    )
}

private fun gearPath(center: Offset, outerRadius: Float, teeth: Int): Path {
    val bodyRadius = outerRadius * 0.72f
    val toothRoot = bodyRadius * 0.9f
    val rootHalfWidth = outerRadius * 0.21f
    val tipHalfWidth = outerRadius * 0.16f

    var gear = Path().apply { addOval(Rect(center, bodyRadius)) }
    val tooth = Path()
    repeat(teeth.coerceAtLeast(0)) { index ->
        val angle = 2 * PI * index / teeth
        val dirX = cos(angle).toFloat()
        val dirY = sin(angle).toFloat()
        fun point(distance: Float, offset: Float) = Offset(
            x = center.x + dirX * distance - dirY * offset,
            y = center.y + dirY * distance + dirX * offset,
        )
        tooth.rewind()
        point(toothRoot, -rootHalfWidth).let { tooth.moveTo(it.x, it.y) }
        point(outerRadius, -tipHalfWidth).let { tooth.lineTo(it.x, it.y) }
        point(outerRadius, tipHalfWidth).let { tooth.lineTo(it.x, it.y) }
        point(toothRoot, rootHalfWidth).let { tooth.lineTo(it.x, it.y) }
        tooth.close()
        gear = Path.combine(PathOperation.Union, gear, tooth)
    }
    val hub = Path().apply { addOval(Rect(center, outerRadius * 0.3f)) }
    return Path.combine(PathOperation.Difference, gear, hub)
}

@Preview
@Composable
private fun ChartCardPreview() {
    ChartCard(
        title = "Vertical bar",
        subtitle = "Statistics of the month",
        action = { ChartOverflowIcon() },
        legend = {
            ChartLegend(items = listOf(LegendItem("Data one", Color(0xFF3375B5))))
        },
    ) {
        Spacer(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFFECEDF4)))
    }
}
