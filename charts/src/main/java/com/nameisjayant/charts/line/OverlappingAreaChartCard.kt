package com.nameisjayant.charts.line

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartChatIcon
import com.nameisjayant.charts.common.ChartCircleButton
import com.nameisjayant.charts.common.ChartInitialsAvatar
import com.nameisjayant.charts.common.ChartLegend
import com.nameisjayant.charts.common.ChartLegendDefaults
import com.nameisjayant.charts.common.ChartLegendStyle
import com.nameisjayant.charts.common.ChartOverflowIcon
import com.nameisjayant.charts.common.LegendItem
import com.nameisjayant.charts.common.chartCardSurface

/** Layout of an [OverlappingAreaChartCard]'s content. Build one with [OverlappingAreaChartCardDefaults.style]. */
@Immutable
public data class OverlappingAreaChartCardStyle(
    /** Fill behind the title, reaching the card's edges. */
    val headerBackgroundColor: Color,
    val filterTextStyle: TextStyle,
    val filterBackgroundColor: Color,
    val selectedFilterTextStyle: TextStyle,
    val selectedFilterBackgroundColor: Color,
    val filterShape: Shape,
    val filterPadding: PaddingValues,
    val filterSpacing: Dp,
    val descriptionTextStyle: TextStyle,
    val nameTextStyle: TextStyle,
    val roleTextStyle: TextStyle,
    val footerTextStyle: TextStyle,
    val avatarNameSpacing: Dp,
    val filtersChartSpacing: Dp,
    val chartDescriptionSpacing: Dp,
    val descriptionLegendSpacing: Dp,
    val legendProfileSpacing: Dp,
    val profileFooterSpacing: Dp,
)

public object OverlappingAreaChartCardDefaults {
    public val HeaderBackgroundColor: Color = Color(0xFF173A84)

    public val FilterTextStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 15.sp)

    public val DescriptionTextStyle: TextStyle = TextStyle(
        color = Color(0xFF4A4A4A),
        fontSize = 17.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.25.sp,
    )

    public val NameTextStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 22.sp)

    public val RoleTextStyle: TextStyle = TextStyle(color = Color(0xFF9E9E9E), fontSize = 15.sp)

    public fun style(
        headerBackgroundColor: Color = HeaderBackgroundColor,
        filterTextStyle: TextStyle = FilterTextStyle,
        filterBackgroundColor: Color = Color(0xFFEBEBEB),
        selectedFilterTextStyle: TextStyle = FilterTextStyle.copy(color = Color.White),
        selectedFilterBackgroundColor: Color = headerBackgroundColor,
        filterShape: Shape = RoundedCornerShape(4.dp),
        filterPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        filterSpacing: Dp = 12.dp,
        descriptionTextStyle: TextStyle = DescriptionTextStyle,
        nameTextStyle: TextStyle = NameTextStyle,
        roleTextStyle: TextStyle = RoleTextStyle,
        footerTextStyle: TextStyle = descriptionTextStyle,
        avatarNameSpacing: Dp = 12.dp,
        filtersChartSpacing: Dp = 24.dp,
        chartDescriptionSpacing: Dp = 16.dp,
        descriptionLegendSpacing: Dp = 16.dp,
        legendProfileSpacing: Dp = 48.dp,
        profileFooterSpacing: Dp = 20.dp,
    ): OverlappingAreaChartCardStyle = OverlappingAreaChartCardStyle(
        headerBackgroundColor = headerBackgroundColor,
        filterTextStyle = filterTextStyle,
        filterBackgroundColor = filterBackgroundColor,
        selectedFilterTextStyle = selectedFilterTextStyle,
        selectedFilterBackgroundColor = selectedFilterBackgroundColor,
        filterShape = filterShape,
        filterPadding = filterPadding,
        filterSpacing = filterSpacing,
        descriptionTextStyle = descriptionTextStyle,
        nameTextStyle = nameTextStyle,
        roleTextStyle = roleTextStyle,
        footerTextStyle = footerTextStyle,
        avatarNameSpacing = avatarNameSpacing,
        filtersChartSpacing = filtersChartSpacing,
        chartDescriptionSpacing = chartDescriptionSpacing,
        descriptionLegendSpacing = descriptionLegendSpacing,
        legendProfileSpacing = legendProfileSpacing,
        profileFooterSpacing = profileFooterSpacing,
    )

    /** [ChartCardDefaults.style] with a white title, to sit on [HeaderBackgroundColor]. */
    public fun cardStyle(
        titleTextStyle: TextStyle = ChartCardDefaults.TitleTextStyle.copy(color = Color.White, lineHeight = 34.sp),
    ): ChartCardStyle = ChartCardDefaults.style(titleTextStyle = titleTextStyle)

    /** [ChartLegendDefaults.style] with square-cornered swatches. */
    public fun legendStyle(): ChartLegendStyle = ChartLegendDefaults.style(indicatorShape = RectangleShape)
}

/**
 * An [OverlappingAreaChart] in a card: a coloured header holding [title] and an [action], a row
 * of [filters] (e.g. regions), the chart, an optional [description], a legend of the series, a
 * profile row (an [avatar], the person's [name] and [role], and a [profileAction] on the right),
 * then an optional [footer] note.
 *
 * The filter row is left out when [filters] is empty, and the profile row when [avatar], [name],
 * [role] and [profileAction] are all `null`. Filters can be tapped only when [onFilterSelected]
 * is given; the one at [selectedFilter] is highlighted.
 */
@Composable
public fun OverlappingAreaChartCard(
    title: String,
    series: List<AreaSeries>,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = { ChartOverflowIcon(color = Color.White, horizontal = true) },
    filters: List<String> = emptyList(),
    selectedFilter: Int? = null,
    onFilterSelected: ((Int) -> Unit)? = null,
    description: String? = null,
    showLegend: Boolean = true,
    name: String? = null,
    role: String? = null,
    avatar: (@Composable () -> Unit)? = null,
    profileAction: (@Composable () -> Unit)? = null,
    footer: String? = null,
    maxValue: Float? = null,
    chartHeight: Dp = OverlappingAreaChartDefaults.ChartHeight,
    cardStyle: ChartCardStyle = OverlappingAreaChartCardDefaults.cardStyle(),
    legendStyle: ChartLegendStyle = OverlappingAreaChartCardDefaults.legendStyle(),
    chartStyle: OverlappingAreaChartStyle = OverlappingAreaChartDefaults.style(),
    contentStyle: OverlappingAreaChartCardStyle = OverlappingAreaChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = OverlappingAreaChartDefaults.AnimationSpec,
) {
    val legendItems = remember(series) { series.map { LegendItem(label = it.label, color = it.color) } }
    Column(modifier = modifier.chartCardSurface(cardStyle)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(contentStyle.headerBackgroundColor)
                .padding(cardStyle.contentPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(text = title, style = cardStyle.titleTextStyle, modifier = Modifier.weight(1f))
            action?.invoke()
        }
        Column(modifier = Modifier.fillMaxWidth().padding(cardStyle.contentPadding)) {
            if (filters.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(contentStyle.filterSpacing),
                    verticalArrangement = Arrangement.spacedBy(contentStyle.filterSpacing),
                ) {
                    filters.forEachIndexed { index, filter ->
                        AreaFilterChip(
                            text = filter,
                            selected = index == selectedFilter,
                            onClick = onFilterSelected?.let { { it(index) } },
                            style = contentStyle,
                        )
                    }
                }
                Spacer(Modifier.height(contentStyle.filtersChartSpacing))
            }
            OverlappingAreaChart(
                series = series,
                maxValue = maxValue,
                style = chartStyle,
                animationSpec = animationSpec,
                modifier = Modifier.fillMaxWidth().height(chartHeight),
            )
            if (description != null) {
                Spacer(Modifier.height(contentStyle.chartDescriptionSpacing))
                BasicText(text = description, style = contentStyle.descriptionTextStyle)
            }
            if (showLegend) {
                Spacer(Modifier.height(contentStyle.descriptionLegendSpacing))
                ChartLegend(items = legendItems, style = legendStyle)
            }
            if (avatar != null || name != null || role != null || profileAction != null) {
                Spacer(Modifier.height(contentStyle.legendProfileSpacing))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
            if (footer != null) {
                Spacer(Modifier.height(contentStyle.profileFooterSpacing))
                BasicText(text = footer, style = contentStyle.footerTextStyle)
            }
        }
    }
}

@Composable
private fun AreaFilterChip(
    text: String,
    selected: Boolean,
    onClick: (() -> Unit)?,
    style: OverlappingAreaChartCardStyle,
) {
    BasicText(
        text = text,
        style = if (selected) style.selectedFilterTextStyle else style.filterTextStyle,
        modifier = Modifier
            .clip(style.filterShape)
            .background(if (selected) style.selectedFilterBackgroundColor else style.filterBackgroundColor)
            .then(if (onClick != null) Modifier.clickable(role = Role.Tab, onClick = onClick) else Modifier)
            .padding(style.filterPadding),
    )
}

@Preview
@Composable
private fun OverlappingAreaChartCardPreview() {
    OverlappingAreaChartCard(
        title = "Estimated Worldwide Population",
        series = listOf(
            AreaSeries(
                label = "2011",
                values = listOf(78f, 40f, 26f, 33f, 0f, 19f, 37f, 48f, 60f, 18f, 18f, 66f, 41f, 49f, 21f, 49f, 0f),
                color = OverlappingAreaChartDefaults.FirstColor,
            ),
            AreaSeries(
                label = "2012",
                values = listOf(28f, 98f, 70f, 78f, 60f, 10f, 61f, 46f, 78f, 18f, 18f, 88f, 40f, 59f, 80f, 60f, 26f),
                color = OverlappingAreaChartDefaults.SecondColor,
            ),
        ),
        filters = listOf("America", "Europe", "Asia"),
        description = "Minim dolor in amet nulla laboris enim dolore consequatt.",
        name = "Ralph Edwards",
        role = "Team Leader",
        avatar = { ChartInitialsAvatar(text = "R", size = 64.dp) },
        profileAction = { ChartCircleButton(onClick = {}) { ChartChatIcon() } },
        footer = "Minim dolor in amet nulla laboris enim dolore consequatt.",
        animationSpec = null,
    )
}
