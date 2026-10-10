package com.nameisjayant.charts.bar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.chartCardFrame

/** Layout of a [RankedBarChartCard]'s content. Build one with [RankedBarChartCardDefaults.style]. */
@Immutable
public data class RankedBarChartCardStyle(
    val descriptionTextStyle: TextStyle,
    val listTitleTextStyle: TextStyle,
    val listCaptionTextStyle: TextStyle,
    val footerTextStyle: TextStyle,
    val titleDescriptionSpacing: Dp,
    val headerListSpacing: Dp,
    val listChartSpacing: Dp,
    val chartFooterSpacing: Dp,
)

public object RankedBarChartCardDefaults {
    public val DescriptionTextStyle: TextStyle = TextStyle(
        color = Color(0xFF4A4A4A),
        fontSize = 17.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.25.sp,
    )

    public val ListTitleTextStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 16.sp)

    public val ListCaptionTextStyle: TextStyle = TextStyle(color = Color(0xFF9E9E9E), fontSize = 16.sp)

    public fun style(
        descriptionTextStyle: TextStyle = DescriptionTextStyle,
        listTitleTextStyle: TextStyle = ListTitleTextStyle,
        listCaptionTextStyle: TextStyle = ListCaptionTextStyle,
        footerTextStyle: TextStyle = descriptionTextStyle,
        titleDescriptionSpacing: Dp = 16.dp,
        headerListSpacing: Dp = 28.dp,
        listChartSpacing: Dp = 12.dp,
        chartFooterSpacing: Dp = 48.dp,
    ): RankedBarChartCardStyle = RankedBarChartCardStyle(
        descriptionTextStyle = descriptionTextStyle,
        listTitleTextStyle = listTitleTextStyle,
        listCaptionTextStyle = listCaptionTextStyle,
        footerTextStyle = footerTextStyle,
        titleDescriptionSpacing = titleDescriptionSpacing,
        headerListSpacing = headerListSpacing,
        listChartSpacing = listChartSpacing,
        chartFooterSpacing = chartFooterSpacing,
    )
}

/**
 * A [RankedBarChart] in a card: a [title] and optional [description], a list header row with a
 * [listTitle] on the left and a [listCaption] (e.g. "8 countries") on the right, the bars, then an
 * optional [footer] note.
 *
 * The list header row is left out when [listTitle] and [listCaption] are both `null`.
 */
@Composable
public fun RankedBarChartCard(
    title: String,
    bars: List<RankedBar>,
    modifier: Modifier = Modifier,
    description: String? = null,
    listTitle: String? = null,
    listCaption: String? = null,
    footer: String? = null,
    maxValue: Float? = null,
    cardStyle: ChartCardStyle = ChartCardDefaults.style(),
    chartStyle: RankedBarChartStyle = RankedBarChartDefaults.style(),
    contentStyle: RankedBarChartCardStyle = RankedBarChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = RankedBarChartDefaults.AnimationSpec,
) {
    Column(modifier = modifier.chartCardFrame(cardStyle)) {
        BasicText(text = title, style = cardStyle.titleTextStyle)
        if (description != null) {
            Spacer(Modifier.height(contentStyle.titleDescriptionSpacing))
            BasicText(text = description, style = contentStyle.descriptionTextStyle)
        }
        Spacer(Modifier.height(contentStyle.headerListSpacing))
        if (listTitle != null || listCaption != null) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BasicText(
                    text = listTitle.orEmpty(),
                    style = contentStyle.listTitleTextStyle,
                    modifier = Modifier.weight(1f),
                )
                if (listCaption != null) BasicText(text = listCaption, style = contentStyle.listCaptionTextStyle)
            }
            Spacer(Modifier.height(contentStyle.listChartSpacing))
        }
        RankedBarChart(
            bars = bars,
            style = chartStyle,
            maxValue = maxValue,
            animationSpec = animationSpec,
            modifier = Modifier.fillMaxWidth(),
        )
        if (footer != null) {
            Spacer(Modifier.height(contentStyle.chartFooterSpacing))
            BasicText(text = footer, style = contentStyle.footerTextStyle)
        }
    }
}

@Preview
@Composable
private fun RankedBarChartCardPreview() {
    RankedBarChartCard(
        title = "Average of the first economies",
        description = "Minim dolor in amet nulla laboris enim dolore consequat.",
        listTitle = "List of countries",
        listCaption = "8 countries",
        bars = listOf(
            RankedBar("Norway", 100f),
            RankedBar("Australia", 91f),
            RankedBar("Switzerland", 81f),
            RankedBar("Netherlands", 74f),
            RankedBar("United States", 63f),
            RankedBar("Germany", 61f),
            RankedBar("New Zealand", 46f),
            RankedBar("Canada", 37f),
        ),
        footer = "Minim dolor in amet nulla laboris enim dolore consequat.",
        animationSpec = null,
    )
}
