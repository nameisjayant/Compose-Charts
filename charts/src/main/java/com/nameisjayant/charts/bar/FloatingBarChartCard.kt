package com.nameisjayant.charts.bar

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartCircleButton
import com.nameisjayant.charts.common.ChartSortIcon
import com.nameisjayant.charts.common.chartCardFrame

/** Layout of a [FloatingBarChartCard]'s content. Build one with [FloatingBarChartCardDefaults.style]. */
@Immutable
public data class FloatingBarChartCardStyle(
    val descriptionTextStyle: TextStyle,
    val captionTextStyle: TextStyle,
    val chipTextStyle: TextStyle,
    val selectedChipTextStyle: TextStyle,
    val chipBackgroundColor: Color,
    val selectedChipBackgroundColor: Color,
    val chipShape: Shape,
    val chipPadding: PaddingValues,
    val chipSpacing: Dp,
    val titleActionSpacing: Dp,
    val titleDescriptionSpacing: Dp,
    val headerChartSpacing: Dp,
    val chartCaptionSpacing: Dp,
    val captionChipsSpacing: Dp,
)

public object FloatingBarChartCardDefaults {
    public val DescriptionTextStyle: TextStyle = TextStyle(
        color = Color(0xFF4A4A4A),
        fontSize = 17.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.25.sp,
    )

    public val CaptionTextStyle: TextStyle = TextStyle(color = Color(0xFF8A8A8A), fontSize = 14.sp)

    public val ChipTextStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 14.sp)

    public val ChipBackgroundColor: Color = Color(0xFFF2F2F2)

    public fun style(
        descriptionTextStyle: TextStyle = DescriptionTextStyle,
        captionTextStyle: TextStyle = CaptionTextStyle,
        chipTextStyle: TextStyle = ChipTextStyle,
        selectedChipTextStyle: TextStyle = chipTextStyle.copy(color = Color.White),
        chipBackgroundColor: Color = ChipBackgroundColor,
        selectedChipBackgroundColor: Color = FloatingBarChartDefaults.AccentColor,
        chipShape: Shape = CircleShape,
        chipPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
        chipSpacing: Dp = 2.dp,
        titleActionSpacing: Dp = 12.dp,
        titleDescriptionSpacing: Dp = 8.dp,
        headerChartSpacing: Dp = 48.dp,
        chartCaptionSpacing: Dp = 36.dp,
        captionChipsSpacing: Dp = 16.dp,
    ): FloatingBarChartCardStyle = FloatingBarChartCardStyle(
        descriptionTextStyle = descriptionTextStyle,
        captionTextStyle = captionTextStyle,
        chipTextStyle = chipTextStyle,
        selectedChipTextStyle = selectedChipTextStyle,
        chipBackgroundColor = chipBackgroundColor,
        selectedChipBackgroundColor = selectedChipBackgroundColor,
        chipShape = chipShape,
        chipPadding = chipPadding,
        chipSpacing = chipSpacing,
        titleActionSpacing = titleActionSpacing,
        titleDescriptionSpacing = titleDescriptionSpacing,
        headerChartSpacing = headerChartSpacing,
        chartCaptionSpacing = chartCaptionSpacing,
        captionChipsSpacing = captionChipsSpacing,
    )
}

/**
 * A [FloatingBarChart] in a card: [title] with an [action] on the right (e.g. a round button
 * holding a [ChartSortIcon]), an optional [description], the chart, an optional [caption] (e.g.
 * the year shown), then a centred row of [ranges] (e.g. `24h`, `1d`, `1w`) as pill chips.
 *
 * The chip row is left out when [ranges] is empty. Chips can be tapped only when
 * [onRangeSelected] is given; the one at [selectedRange] is filled.
 */
@Composable
public fun FloatingBarChartCard(
    title: String,
    bars: List<FloatingBar>,
    modifier: Modifier = Modifier,
    description: String? = null,
    action: (@Composable () -> Unit)? = null,
    caption: String? = null,
    ranges: List<String> = emptyList(),
    selectedRange: Int? = null,
    onRangeSelected: ((Int) -> Unit)? = null,
    maxValue: Float? = null,
    gridSteps: Int = 4,
    labelFormatter: (Float) -> String = FloatingBarChartDefaults::formatValue,
    chartHeight: Dp = FloatingBarChartDefaults.ChartHeight,
    cardStyle: ChartCardStyle = ChartCardDefaults.style(),
    chartStyle: FloatingBarChartStyle = FloatingBarChartDefaults.style(),
    contentStyle: FloatingBarChartCardStyle = FloatingBarChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = FloatingBarChartDefaults.AnimationSpec,
) {
    Column(modifier = modifier.chartCardFrame(cardStyle)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                BasicText(text = title, style = cardStyle.titleTextStyle)
                if (description != null) {
                    Spacer(Modifier.height(contentStyle.titleDescriptionSpacing))
                    BasicText(text = description, style = contentStyle.descriptionTextStyle)
                }
            }
            if (action != null) {
                Spacer(Modifier.width(contentStyle.titleActionSpacing))
                action()
            }
        }
        Spacer(Modifier.height(contentStyle.headerChartSpacing))
        FloatingBarChart(
            bars = bars,
            maxValue = maxValue,
            gridSteps = gridSteps,
            labelFormatter = labelFormatter,
            style = chartStyle,
            animationSpec = animationSpec,
            modifier = Modifier.fillMaxWidth().height(chartHeight),
        )
        if (caption != null) {
            Spacer(Modifier.height(contentStyle.chartCaptionSpacing))
            BasicText(text = caption, style = contentStyle.captionTextStyle)
        }
        if (ranges.isNotEmpty()) {
            Spacer(Modifier.height(contentStyle.captionChipsSpacing))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(contentStyle.chipSpacing, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(contentStyle.chipSpacing),
            ) {
                ranges.forEachIndexed { index, range ->
                    RangeChip(
                        text = range,
                        selected = index == selectedRange,
                        onClick = onRangeSelected?.let { { it(index) } },
                        style = contentStyle,
                    )
                }
            }
        }
    }
}

@Composable
private fun RangeChip(
    text: String,
    selected: Boolean,
    onClick: (() -> Unit)?,
    style: FloatingBarChartCardStyle,
) {
    BasicText(
        text = text,
        style = if (selected) style.selectedChipTextStyle else style.chipTextStyle,
        maxLines = 1,
        modifier = Modifier
            .clip(style.chipShape)
            .background(if (selected) style.selectedChipBackgroundColor else style.chipBackgroundColor)
            .then(if (onClick != null) Modifier.clickable(role = Role.Tab, onClick = onClick) else Modifier)
            .padding(style.chipPadding),
    )
}

@Preview
@Composable
private fun FloatingBarChartCardPreview() {
    val primary = FloatingBarChartDefaults.PrimaryColor
    val light = FloatingBarChartDefaults.LightColor
    FloatingBarChartCard(
        title = "Statistic",
        description = "Minim dolor in amet nulla laboris enim dolore consequatt.",
        action = { ChartCircleButton(onClick = {}) { ChartSortIcon() } },
        bars = listOf(
            FloatingBar(2800f, 5000f, primary),
            FloatingBar(1800f, 3000f, light),
            FloatingBar(3200f, 6000f, primary),
            FloatingBar(3800f, 5100f, light),
            FloatingBar(500f, 5000f, FloatingBarChartDefaults.AccentColor),
            FloatingBar(2200f, 7800f, light),
            FloatingBar(1700f, 4800f, primary),
            FloatingBar(2100f, 5700f, light),
            FloatingBar(3100f, 4400f, primary),
        ),
        caption = "Year 2021",
        ranges = listOf("24h", "1d", "3d", "1w", "3w", "1m"),
        selectedRange = 2,
        animationSpec = null,
    )
}
