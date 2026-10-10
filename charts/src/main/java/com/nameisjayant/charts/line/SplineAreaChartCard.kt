package com.nameisjayant.charts.line

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.charts.common.ChartBarsIcon
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.chartCardSurface

/** Layout of a [SplineAreaChartCard]'s headline. Build one with [SplineAreaChartCardDefaults.style]. */
@Immutable
public data class SplineAreaChartCardStyle(
    val valueTextStyle: TextStyle,
    val badgeTextStyle: TextStyle,
    val badgeColor: Color,
    val badgeShape: Shape,
    val badgePadding: PaddingValues,
    /** Tile behind the action in the top-right corner. */
    val actionBackgroundColor: Color,
    val actionShape: Shape,
    val actionSize: Dp,
    /** Gap above the title, so it clears the action tile. */
    val titleTopSpacing: Dp,
    val titleValueSpacing: Dp,
    val valueBadgeSpacing: Dp,
    val headerChartSpacing: Dp,
)

public object SplineAreaChartCardDefaults {
    public val BadgeColor: Color = Color(0xFF255E73)
    public val ActionBackgroundColor: Color = Color(0xFFF4F6FA)
    public val ActionIconColor: Color = BadgeColor

    public val TitleTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 18.sp,
    )

    public val ValueTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 38.sp,
        fontWeight = FontWeight.Bold,
    )

    public val BadgeTextStyle: TextStyle = TextStyle(
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
    )

    public fun style(
        valueTextStyle: TextStyle = ValueTextStyle,
        badgeTextStyle: TextStyle = BadgeTextStyle,
        badgeColor: Color = BadgeColor,
        badgeShape: Shape = RoundedCornerShape(percent = 50),
        badgePadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 7.dp),
        actionBackgroundColor: Color = ActionBackgroundColor,
        actionShape: Shape = RoundedCornerShape(12.dp),
        actionSize: Dp = 46.dp,
        titleTopSpacing: Dp = 32.dp,
        titleValueSpacing: Dp = 8.dp,
        valueBadgeSpacing: Dp = 12.dp,
        headerChartSpacing: Dp = 36.dp,
    ): SplineAreaChartCardStyle = SplineAreaChartCardStyle(
        valueTextStyle = valueTextStyle,
        badgeTextStyle = badgeTextStyle,
        badgeColor = badgeColor,
        badgeShape = badgeShape,
        badgePadding = badgePadding,
        actionBackgroundColor = actionBackgroundColor,
        actionShape = actionShape,
        actionSize = actionSize,
        titleTopSpacing = titleTopSpacing,
        titleValueSpacing = titleValueSpacing,
        valueBadgeSpacing = valueBadgeSpacing,
        headerChartSpacing = headerChartSpacing,
    )

    /** [ChartCardDefaults.style] with a lighter title and tighter padding around the action tile. */
    public fun cardStyle(
        titleTextStyle: TextStyle = TitleTextStyle,
        contentPadding: PaddingValues = PaddingValues(20.dp),
    ): ChartCardStyle = ChartCardDefaults.style(
        titleTextStyle = titleTextStyle,
        contentPadding = contentPadding,
    )
}

/**
 * A headline figure over its trend: [title], a large [value] and an optional [badge] (e.g. the
 * change since last period), centred, with an optional [action] in a tile in the top-right corner,
 * then a [SplineAreaChart] of [values] reaching the card's left, right and bottom edges.
 *
 * [ChartCardStyle.contentPadding] pads only the headline; the chart is full bleed.
 */
@Composable
public fun SplineAreaChartCard(
    title: String,
    value: String,
    values: List<Float>,
    modifier: Modifier = Modifier,
    badge: String? = null,
    action: (@Composable () -> Unit)? = { ChartBarsIcon(color = SplineAreaChartCardDefaults.ActionIconColor) },
    minValue: Float? = null,
    maxValue: Float? = null,
    chartHeight: Dp = SplineAreaChartDefaults.ChartHeight,
    cardStyle: ChartCardStyle = SplineAreaChartCardDefaults.cardStyle(),
    chartStyle: SplineAreaChartStyle = SplineAreaChartDefaults.style(),
    contentStyle: SplineAreaChartCardStyle = SplineAreaChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = SplineAreaChartDefaults.AnimationSpec,
) {
    Column(modifier = modifier.chartCardSurface(cardStyle)) {
        Box(modifier = Modifier.fillMaxWidth().padding(cardStyle.contentPadding)) {
            if (action != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(contentStyle.actionSize)
                        .background(contentStyle.actionBackgroundColor, contentStyle.actionShape),
                    contentAlignment = Alignment.Center,
                ) { action() }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = contentStyle.titleTopSpacing),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BasicText(text = title, style = cardStyle.titleTextStyle)
                Spacer(Modifier.height(contentStyle.titleValueSpacing))
                BasicText(text = value, style = contentStyle.valueTextStyle)
                if (badge != null) {
                    Spacer(Modifier.height(contentStyle.valueBadgeSpacing))
                    BasicText(
                        text = badge,
                        style = contentStyle.badgeTextStyle,
                        modifier = Modifier
                            .background(contentStyle.badgeColor, contentStyle.badgeShape)
                            .padding(contentStyle.badgePadding),
                    )
                }
            }
        }
        Spacer(Modifier.height(contentStyle.headerChartSpacing))
        SplineAreaChart(
            values = values,
            minValue = minValue,
            maxValue = maxValue,
            style = chartStyle,
            animationSpec = animationSpec,
            modifier = Modifier.fillMaxWidth().height(chartHeight),
        )
    }
}

@Preview
@Composable
private fun SplineAreaChartCardPreview() {
    SplineAreaChartCard(
        title = "Expected earnings",
        value = "€682.5",
        badge = "+2.45%",
        values = listOf(180f, 148f, 194f, 184f, 255f, 120f, 200f, 248f, 194f, 200f, 140f, 196f, 192f, 248f),
        animationSpec = null,
    )
}
