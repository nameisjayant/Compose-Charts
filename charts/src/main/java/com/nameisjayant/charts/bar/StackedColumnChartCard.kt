package com.nameisjayant.charts.bar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartChatIcon
import com.nameisjayant.charts.common.ChartInitialsAvatar
import com.nameisjayant.charts.common.ChartLegend
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.charts.common.ChartLegendStyle
import com.nameisjayant.charts.common.ChartSparkIcon
import com.nameisjayant.charts.common.LegendItem
import com.nameisjayant.charts.common.chartCardSurface

/** Layout of a [StackedColumnChartCard]'s content. Build one with [StackedColumnChartCardDefaults.style]. */
@Immutable
public data class StackedColumnChartCardStyle(
    val tabTextStyle: TextStyle,
    val selectedTabTextStyle: TextStyle,
    val selectedTabBackgroundColor: Color,
    val tabShape: Shape,
    val tabPadding: PaddingValues,
    val descriptionTextStyle: TextStyle,
    /** Fill behind the profile row, reaching the card's edges. */
    val footerBackgroundColor: Color,
    val footerPadding: PaddingValues,
    val nameTextStyle: TextStyle,
    val roleTextStyle: TextStyle,
    val avatarNameSpacing: Dp,
    val headerTabsSpacing: Dp,
    val tabsChartSpacing: Dp,
    val chartLegendSpacing: Dp,
    val legendDescriptionSpacing: Dp,
)

public object StackedColumnChartCardDefaults {
    public val TabTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
    )

    public val SelectedTabBackgroundColor: Color = Color(0xFFDDF5FC)

    public val DescriptionTextStyle: TextStyle = TextStyle(
        color = Color(0xFF4A4A4A),
        fontSize = 17.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.25.sp,
    )

    public val FooterBackgroundColor: Color = Color(0xFFF0FAFD)

    public val NameTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
    )

    public val RoleTextStyle: TextStyle = TextStyle(color = Color(0xFF1A1A1A), fontSize = 14.sp)

    public val SparklineColor: Color = Color(0xFF4499C8)
    public val SparklineHaloColor: Color = Color(0xFFC8EBF7)

    /** A rising, wiggly trend for the default header [StackedColumnSparkline]. */
    public val SparklinePoints: List<Float> = listOf(
        0.05f, 0.35f, 0.55f, 0.62f, 0.5f, 0.66f, 0.6f, 0.48f, 0.52f, 0.44f, 0.5f, 0.42f, 0.56f,
        0.7f, 0.66f, 0.78f, 0.72f, 0.6f, 0.68f, 0.52f, 0.45f, 0.62f, 0.7f, 0.88f, 0.76f, 0.92f,
        0.84f, 0.95f, 1f,
    )

    public fun style(
        tabTextStyle: TextStyle = TabTextStyle,
        selectedTabTextStyle: TextStyle = tabTextStyle,
        selectedTabBackgroundColor: Color = SelectedTabBackgroundColor,
        tabShape: Shape = RoundedCornerShape(12.dp),
        tabPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        descriptionTextStyle: TextStyle = DescriptionTextStyle,
        footerBackgroundColor: Color = FooterBackgroundColor,
        footerPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
        nameTextStyle: TextStyle = NameTextStyle,
        roleTextStyle: TextStyle = RoleTextStyle,
        avatarNameSpacing: Dp = 12.dp,
        headerTabsSpacing: Dp = 28.dp,
        tabsChartSpacing: Dp = 28.dp,
        chartLegendSpacing: Dp = 24.dp,
        legendDescriptionSpacing: Dp = 24.dp,
    ): StackedColumnChartCardStyle = StackedColumnChartCardStyle(
        tabTextStyle = tabTextStyle,
        selectedTabTextStyle = selectedTabTextStyle,
        selectedTabBackgroundColor = selectedTabBackgroundColor,
        tabShape = tabShape,
        tabPadding = tabPadding,
        descriptionTextStyle = descriptionTextStyle,
        footerBackgroundColor = footerBackgroundColor,
        footerPadding = footerPadding,
        nameTextStyle = nameTextStyle,
        roleTextStyle = roleTextStyle,
        avatarNameSpacing = avatarNameSpacing,
        headerTabsSpacing = headerTabsSpacing,
        tabsChartSpacing = tabsChartSpacing,
        chartLegendSpacing = chartLegendSpacing,
        legendDescriptionSpacing = legendDescriptionSpacing,
    )

    /** [ChartCardDefaults.style] with a title that wraps onto two comfortably spaced lines. */
    public fun cardStyle(
        titleTextStyle: TextStyle = ChartCardDefaults.TitleTextStyle.copy(lineHeight = 34.sp),
    ): ChartCardStyle = ChartCardDefaults.style(titleTextStyle = titleTextStyle)

    /** [ChartLegendDefaults.style] with larger, regular-weight labels. */
    public fun legendStyle(
        textStyle: TextStyle = ChartLegendDefaults.TextStyle.copy(fontSize = 17.sp, fontWeight = FontWeight.Normal),
    ): ChartLegendStyle = ChartLegendDefaults.style(textStyle = textStyle)
}

/** The glowing trend line in a [StackedColumnChartCard]'s top-right corner. */
@Composable
public fun StackedColumnSparkline(
    modifier: Modifier = Modifier,
    color: Color = StackedColumnChartCardDefaults.SparklineColor,
    haloColor: Color = StackedColumnChartCardDefaults.SparklineHaloColor,
    points: List<Float> = StackedColumnChartCardDefaults.SparklinePoints,
) {
    ChartSparkIcon(
        modifier = modifier,
        color = color,
        size = DpSize(width = 96.dp, height = 36.dp),
        strokeWidth = 2.dp,
        points = points,
        haloColor = haloColor,
        haloWidth = 7.dp,
    )
}

/**
 * A [StackedColumnChart] in a card: [title] with an [action] (a glowing sparkline by default), a
 * row of [tabs] (e.g. months), the columns, a legend of the series, an optional [description],
 * then a tinted profile row (an [avatar], the person's [name] and [role], and a [profileAction]
 * on the right) along the card's bottom edge.
 *
 * The tab row is left out when [tabs] is empty, and the profile row when [avatar], [name], [role]
 * and [profileAction] are all `null`. Tabs can be tapped only when [onTabSelected] is given; the
 * one at [selectedTab] is highlighted.
 */
@Composable
public fun StackedColumnChartCard(
    title: String,
    series: List<BarSeries>,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = { StackedColumnSparkline() },
    tabs: List<String> = emptyList(),
    selectedTab: Int? = null,
    onTabSelected: ((Int) -> Unit)? = null,
    showLegend: Boolean = true,
    description: String? = null,
    name: String? = null,
    role: String? = null,
    avatar: (@Composable () -> Unit)? = null,
    profileAction: (@Composable () -> Unit)? = null,
    maxValue: Float? = null,
    chartHeight: Dp = StackedColumnChartDefaults.ChartHeight,
    cardStyle: ChartCardStyle = StackedColumnChartCardDefaults.cardStyle(),
    legendStyle: ChartLegendStyle = StackedColumnChartCardDefaults.legendStyle(),
    chartStyle: StackedColumnChartStyle = StackedColumnChartDefaults.style(),
    contentStyle: StackedColumnChartCardStyle = StackedColumnChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = StackedColumnChartDefaults.AnimationSpec,
) {
    val legendItems = remember(series) { series.map { LegendItem(label = it.label, color = it.color) } }
    Column(modifier = modifier.chartCardSurface(cardStyle)) {
        Column(modifier = Modifier.fillMaxWidth().padding(cardStyle.contentPadding)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BasicText(text = title, style = cardStyle.titleTextStyle, modifier = Modifier.weight(1f))
                action?.invoke()
            }
            Spacer(Modifier.height(contentStyle.headerTabsSpacing))
            if (tabs.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    tabs.forEachIndexed { index, tab ->
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            ColumnTab(
                                text = tab,
                                selected = index == selectedTab,
                                onClick = onTabSelected?.let { { it(index) } },
                                style = contentStyle,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(contentStyle.tabsChartSpacing))
            }
            StackedColumnChart(
                series = series,
                maxValue = maxValue,
                style = chartStyle,
                animationSpec = animationSpec,
                modifier = Modifier.fillMaxWidth().height(chartHeight),
            )
            if (showLegend) {
                Spacer(Modifier.height(contentStyle.chartLegendSpacing))
                ChartLegend(items = legendItems, style = legendStyle)
            }
            if (description != null) {
                Spacer(Modifier.height(contentStyle.legendDescriptionSpacing))
                BasicText(text = description, style = contentStyle.descriptionTextStyle)
            }
        }
        if (avatar != null || name != null || role != null || profileAction != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(contentStyle.footerBackgroundColor)
                    .padding(contentStyle.footerPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (avatar != null) {
                    avatar()
                    Spacer(Modifier.width(contentStyle.avatarNameSpacing))
                }
                Column(modifier = Modifier.weight(1f)) {
                    if (name != null) BasicText(text = name, style = contentStyle.nameTextStyle)
                    if (role != null) BasicText(text = role, style = contentStyle.roleTextStyle)
                }
                profileAction?.invoke()
            }
        }
    }
}

@Composable
private fun ColumnTab(
    text: String,
    selected: Boolean,
    onClick: (() -> Unit)?,
    style: StackedColumnChartCardStyle,
) {
    BasicText(
        text = text,
        style = if (selected) style.selectedTabTextStyle else style.tabTextStyle,
        maxLines = 1,
        modifier = Modifier
            .clip(style.tabShape)
            .background(if (selected) style.selectedTabBackgroundColor else Color.Transparent)
            .then(if (onClick != null) Modifier.clickable(role = Role.Tab, onClick = onClick) else Modifier)
            .padding(style.tabPadding),
    )
}

@Preview
@Composable
private fun StackedColumnChartCardPreview() {
    StackedColumnChartCard(
        title = "Stacked column chart",
        series = listOf(
            BarSeries("Apple", listOf(100f, 73f, 55f, 85f, 27f), StackedColumnChartDefaults.FirstColor),
            BarSeries("Banana", listOf(100f, 73f, 55f, 85f, 27f), StackedColumnChartDefaults.SecondColor),
        ),
        tabs = listOf("Nov", "Dec", "Jan", "Feb", "Mar", "Apr"),
        selectedTab = 1,
        description = "Minim dolor in amet nulla laboris enim dolore consequatt.",
        name = "Jerome Bell",
        role = "Marketing Coordinator",
        avatar = {
            ChartInitialsAvatar(
                text = "J",
                modifier = Modifier.border(2.dp, Color.Black, CircleShape).padding(4.dp),
            )
        },
        profileAction = { ChartChatIcon(size = 24.dp) },
        animationSpec = null,
    )
}
