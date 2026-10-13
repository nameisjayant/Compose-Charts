package com.nameisjayant.charts.heatmap

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartCircleButton
import com.nameisjayant.charts.common.ChartSettingsIcon
import com.nameisjayant.charts.common.chartCardSurface

/** Layout of a [HeatmapChartCard]'s content. Build one with [HeatmapChartCardDefaults.style]. */
@Immutable
public data class HeatmapChartCardStyle(
    val subtitleTextStyle: TextStyle,
    val descriptionTextStyle: TextStyle,
    val tabTextStyle: TextStyle,
    val selectedTabTextStyle: TextStyle,
    val tabBorderColor: Color,
    val selectedTabBackgroundColor: Color,
    val tabBorderWidth: Dp,
    val tabShape: Shape,
    val tabPadding: PaddingValues,
    val tabSpacing: Dp,
    val noteTextStyle: TextStyle,
    val legendTextStyle: TextStyle,
    val legendSwatchSize: Dp,
    val legendSwatchCornerRadius: Dp,
    val legendSpacing: Dp,
    val footerTextStyle: TextStyle,
    val footerBackgroundColor: Color,
    val footerShape: Shape,
    val footerHeight: Dp,
    /** Space around the footer button, which sits outside the card's content padding. */
    val footerMargin: PaddingValues,
    val titleActionSpacing: Dp,
    val titleSubtitleSpacing: Dp,
    val headerTabsSpacing: Dp,
    val tabsDescriptionSpacing: Dp,
    val descriptionChartSpacing: Dp,
    val chartLegendSpacing: Dp,
    val noteLegendSpacing: Dp,
)

public object HeatmapChartCardDefaults {
    public val SubtitleTextStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 17.sp)

    public val DescriptionTextStyle: TextStyle = TextStyle(
        color = Color(0xFF4A4A4A),
        fontSize = 17.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.25.sp,
    )

    public val TabTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
    )

    public val NoteTextStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 14.sp, lineHeight = 20.sp)

    public val LegendTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
    )

    public val FooterTextStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 15.sp)

    public val FooterBackgroundColor: Color = Color(0xFFF2F2F2)

    public fun style(
        subtitleTextStyle: TextStyle = SubtitleTextStyle,
        descriptionTextStyle: TextStyle = DescriptionTextStyle,
        tabTextStyle: TextStyle = TabTextStyle,
        selectedTabTextStyle: TextStyle = tabTextStyle.copy(color = Color.White),
        tabBorderColor: Color = Color(0xFF000000),
        selectedTabBackgroundColor: Color = HeatmapChartDefaults.MediumColor,
        tabBorderWidth: Dp = 2.dp,
        tabShape: Shape = CircleShape,
        tabPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
        tabSpacing: Dp = 12.dp,
        noteTextStyle: TextStyle = NoteTextStyle,
        legendTextStyle: TextStyle = LegendTextStyle,
        legendSwatchSize: Dp = 14.dp,
        legendSwatchCornerRadius: Dp = 3.dp,
        legendSpacing: Dp = 10.dp,
        footerTextStyle: TextStyle = FooterTextStyle,
        footerBackgroundColor: Color = FooterBackgroundColor,
        footerShape: Shape = RoundedCornerShape(12.dp),
        footerHeight: Dp = 48.dp,
        footerMargin: PaddingValues = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        titleActionSpacing: Dp = 12.dp,
        titleSubtitleSpacing: Dp = 12.dp,
        headerTabsSpacing: Dp = 32.dp,
        tabsDescriptionSpacing: Dp = 16.dp,
        descriptionChartSpacing: Dp = 32.dp,
        chartLegendSpacing: Dp = 20.dp,
        noteLegendSpacing: Dp = 12.dp,
    ): HeatmapChartCardStyle = HeatmapChartCardStyle(
        subtitleTextStyle = subtitleTextStyle,
        descriptionTextStyle = descriptionTextStyle,
        tabTextStyle = tabTextStyle,
        selectedTabTextStyle = selectedTabTextStyle,
        tabBorderColor = tabBorderColor,
        selectedTabBackgroundColor = selectedTabBackgroundColor,
        tabBorderWidth = tabBorderWidth,
        tabShape = tabShape,
        tabPadding = tabPadding,
        tabSpacing = tabSpacing,
        noteTextStyle = noteTextStyle,
        legendTextStyle = legendTextStyle,
        legendSwatchSize = legendSwatchSize,
        legendSwatchCornerRadius = legendSwatchCornerRadius,
        legendSpacing = legendSpacing,
        footerTextStyle = footerTextStyle,
        footerBackgroundColor = footerBackgroundColor,
        footerShape = footerShape,
        footerHeight = footerHeight,
        footerMargin = footerMargin,
        titleActionSpacing = titleActionSpacing,
        titleSubtitleSpacing = titleSubtitleSpacing,
        headerTabsSpacing = headerTabsSpacing,
        tabsDescriptionSpacing = tabsDescriptionSpacing,
        descriptionChartSpacing = descriptionChartSpacing,
        chartLegendSpacing = chartLegendSpacing,
        noteLegendSpacing = noteLegendSpacing,
    )
}

/**
 * A [HeatmapChart] in a card: [title] with an [action] on the right (e.g. a round button holding a
 * [ChartSettingsIcon]), an optional [subtitle], a row of [tabs] (e.g. years) as outlined pills, an
 * optional [description], the chart, a row with a [note] on the left and a [HeatmapLegend] on the
 * right, and a full-width [footerText] button at the bottom.
 *
 * The tab row is left out when [tabs] is empty, and tabs can be tapped only when [onTabSelected]
 * is given; the one at [selectedTab] is filled. The legend is left out when [showLegend] is false,
 * and the footer button when [footerText] is `null`.
 */
@Composable
public fun HeatmapChartCard(
    title: String,
    values: List<List<Float>>,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
    tabs: List<String> = emptyList(),
    selectedTab: Int? = null,
    onTabSelected: ((Int) -> Unit)? = null,
    description: String? = null,
    columnLabels: List<String> = emptyList(),
    colors: List<Color> = HeatmapChartDefaults.Colors,
    maxValue: Float? = null,
    note: String? = null,
    showLegend: Boolean = true,
    lessLabel: String = "Less",
    moreLabel: String = "More",
    footerText: String? = null,
    onFooterClick: (() -> Unit)? = null,
    cardStyle: ChartCardStyle = ChartCardDefaults.style(),
    chartStyle: HeatmapChartStyle = HeatmapChartDefaults.style(),
    contentStyle: HeatmapChartCardStyle = HeatmapChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = HeatmapChartDefaults.AnimationSpec,
) {
    Column(modifier = modifier.chartCardSurface(cardStyle)) {
        Column(modifier = Modifier.fillMaxWidth().padding(cardStyle.contentPadding)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(text = title, style = cardStyle.titleTextStyle)
                    if (subtitle != null) {
                        Spacer(Modifier.height(contentStyle.titleSubtitleSpacing))
                        BasicText(text = subtitle, style = contentStyle.subtitleTextStyle)
                    }
                }
                if (action != null) {
                    Spacer(Modifier.width(contentStyle.titleActionSpacing))
                    action()
                }
            }
            if (tabs.isNotEmpty()) {
                Spacer(Modifier.height(contentStyle.headerTabsSpacing))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(contentStyle.tabSpacing),
                    verticalArrangement = Arrangement.spacedBy(contentStyle.tabSpacing),
                ) {
                    tabs.forEachIndexed { index, tab ->
                        HeatmapTab(
                            text = tab,
                            selected = index == selectedTab,
                            onClick = onTabSelected?.let { { it(index) } },
                            style = contentStyle,
                        )
                    }
                }
            }
            if (description != null) {
                Spacer(Modifier.height(contentStyle.tabsDescriptionSpacing))
                BasicText(text = description, style = contentStyle.descriptionTextStyle)
            }
            Spacer(Modifier.height(contentStyle.descriptionChartSpacing))
            HeatmapChart(
                values = values,
                colors = colors,
                columnLabels = columnLabels,
                maxValue = maxValue,
                style = chartStyle,
                animationSpec = animationSpec,
                modifier = Modifier.fillMaxWidth(),
            )
            if (note != null || showLegend) {
                Spacer(Modifier.height(contentStyle.chartLegendSpacing))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (note != null) BasicText(text = note, style = contentStyle.noteTextStyle)
                    }
                    if (showLegend) {
                        Spacer(Modifier.width(contentStyle.noteLegendSpacing))
                        HeatmapLegend(
                            colors = colors,
                            lessLabel = lessLabel,
                            moreLabel = moreLabel,
                            textStyle = contentStyle.legendTextStyle,
                            swatchSize = contentStyle.legendSwatchSize,
                            swatchCornerRadius = contentStyle.legendSwatchCornerRadius,
                            spacing = contentStyle.legendSpacing,
                        )
                    }
                }
            }
        }
        if (footerText != null) {
            Box(
                modifier = Modifier
                    .padding(contentStyle.footerMargin)
                    .fillMaxWidth()
                    .height(contentStyle.footerHeight)
                    .clip(contentStyle.footerShape)
                    .background(contentStyle.footerBackgroundColor)
                    .then(
                        if (onFooterClick != null) Modifier.clickable(role = Role.Button, onClick = onFooterClick) else Modifier,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(text = footerText, style = contentStyle.footerTextStyle)
            }
        }
    }
}

/** [lessLabel], a swatch of every one of [colors] from emptiest to busiest, then [moreLabel]. */
@Composable
public fun HeatmapLegend(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    lessLabel: String = "Less",
    moreLabel: String = "More",
    textStyle: TextStyle = HeatmapChartCardDefaults.LegendTextStyle,
    swatchSize: Dp = 14.dp,
    swatchCornerRadius: Dp = 3.dp,
    spacing: Dp = 10.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(text = lessLabel, style = textStyle)
        colors.forEach { color ->
            Spacer(
                Modifier
                    .size(swatchSize)
                    .background(color, RoundedCornerShape(swatchCornerRadius)),
            )
        }
        BasicText(text = moreLabel, style = textStyle)
    }
}

@Composable
private fun HeatmapTab(
    text: String,
    selected: Boolean,
    onClick: (() -> Unit)?,
    style: HeatmapChartCardStyle,
) {
    val borderColor = if (selected) style.selectedTabBackgroundColor else style.tabBorderColor
    BasicText(
        text = text,
        style = if (selected) style.selectedTabTextStyle else style.tabTextStyle,
        maxLines = 1,
        modifier = Modifier
            .clip(style.tabShape)
            .then(if (selected) Modifier.background(style.selectedTabBackgroundColor) else Modifier)
            .border(BorderStroke(style.tabBorderWidth, borderColor), style.tabShape)
            .then(if (onClick != null) Modifier.clickable(role = Role.Tab, onClick = onClick) else Modifier)
            .padding(style.tabPadding),
    )
}

@Preview
@Composable
private fun HeatmapChartCardPreview() {
    HeatmapChartCard(
        title = "Sales per employee per month",
        subtitle = "263 contributions in the last year",
        action = { ChartCircleButton(onClick = {}) { ChartSettingsIcon() } },
        tabs = listOf("2016", "2017", "2018", "2019"),
        selectedTab = 0,
        description = "Minim dolor in amet nulla laboris enim dolore consequatt.",
        values = listOf(
            listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
            listOf(0f, 7f, 0f, 12f, 14f, 0f, 0f, 3f, 0f, 0f, 0f),
            listOf(0f, 8f, 0f, 0f, 13f, 0f, 2f, 4f, 0f, 11f, 12f),
            listOf(0f, 13f, 0f, 0f, 3f, 14f, 0f, 0f, 4f, 2f, 0f),
            listOf(0f, 0f, 3f, 6f, 2f, 12f, 11f, 4f, 0f, 0f, 0f),
            listOf(0f, 4f, 0f, 2f, 0f, 0f, 13f, 14f, 0f, 0f, 0f),
        ),
        columnLabels = listOf("Nov", "Dec", "Jan", "Feb", "Mar", "Apr"),
        note = "Learn how we count contributions",
        footerText = "Show more activity",
        onFooterClick = {},
        animationSpec = null,
    )
}
