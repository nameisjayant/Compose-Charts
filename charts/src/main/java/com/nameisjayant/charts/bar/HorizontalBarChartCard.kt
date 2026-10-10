package com.nameisjayant.charts.bar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.charts.common.ChartCardDefaults
import com.nameisjayant.charts.common.ChartCardStyle
import com.nameisjayant.charts.common.ChartInitialsAvatar
import com.nameisjayant.charts.common.ChartPillButton
import com.nameisjayant.charts.common.chartCardFrame

/** Layout of a [HorizontalBarChartCard]'s content. Build one with [HorizontalBarChartCardDefaults.style]. */
@Immutable
public data class HorizontalBarChartCardStyle(
    val nameTextStyle: TextStyle,
    val roleTextStyle: TextStyle,
    val descriptionTextStyle: TextStyle,
    /** Most lines the description may take before it is cut off with an ellipsis. */
    val descriptionMaxLines: Int,
    val avatarNameSpacing: Dp,
    val titleProfileSpacing: Dp,
    val profileChartSpacing: Dp,
    val chartDescriptionSpacing: Dp,
)

public object HorizontalBarChartCardDefaults {
    public val NameTextStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 20.sp)

    public val RoleTextStyle: TextStyle = TextStyle(color = Color(0xFF9E9E9E), fontSize = 14.sp)

    public val DescriptionTextStyle: TextStyle = TextStyle(
        color = Color(0xFF000000),
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.25.sp,
    )

    public fun style(
        nameTextStyle: TextStyle = NameTextStyle,
        roleTextStyle: TextStyle = RoleTextStyle,
        descriptionTextStyle: TextStyle = DescriptionTextStyle,
        descriptionMaxLines: Int = 2,
        avatarNameSpacing: Dp = 12.dp,
        titleProfileSpacing: Dp = 40.dp,
        profileChartSpacing: Dp = 40.dp,
        chartDescriptionSpacing: Dp = 40.dp,
    ): HorizontalBarChartCardStyle = HorizontalBarChartCardStyle(
        nameTextStyle = nameTextStyle,
        roleTextStyle = roleTextStyle,
        descriptionTextStyle = descriptionTextStyle,
        descriptionMaxLines = descriptionMaxLines,
        avatarNameSpacing = avatarNameSpacing,
        titleProfileSpacing = titleProfileSpacing,
        profileChartSpacing = profileChartSpacing,
        chartDescriptionSpacing = chartDescriptionSpacing,
    )
}

/**
 * A [HorizontalBarChart] in a card about one person: a [title], a profile row (an [avatar], the
 * person's [name] and [role], and an [action] such as a [ChartPillButton] on the right), the bars,
 * then an optional [description] cut off after [HorizontalBarChartCardStyle.descriptionMaxLines].
 *
 * The profile row is left out when [avatar], [name], [role] and [action] are all `null`.
 */
@Composable
public fun HorizontalBarChartCard(
    title: String,
    bars: List<HorizontalBar>,
    modifier: Modifier = Modifier,
    name: String? = null,
    role: String? = null,
    avatar: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    description: String? = null,
    maxValue: Float? = null,
    cardStyle: ChartCardStyle = ChartCardDefaults.style(),
    chartStyle: HorizontalBarChartStyle = HorizontalBarChartDefaults.style(),
    contentStyle: HorizontalBarChartCardStyle = HorizontalBarChartCardDefaults.style(),
    animationSpec: AnimationSpec<Float>? = HorizontalBarChartDefaults.AnimationSpec,
) {
    Column(modifier = modifier.chartCardFrame(cardStyle)) {
        BasicText(text = title, style = cardStyle.titleTextStyle)
        if (avatar != null || name != null || role != null || action != null) {
            Spacer(Modifier.height(contentStyle.titleProfileSpacing))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (avatar != null) {
                    avatar()
                    Spacer(Modifier.width(contentStyle.avatarNameSpacing))
                }
                Column(modifier = Modifier.weight(1f)) {
                    if (name != null) BasicText(text = name, style = contentStyle.nameTextStyle)
                    if (role != null) BasicText(text = role, style = contentStyle.roleTextStyle)
                }
                action?.invoke()
            }
        }
        Spacer(Modifier.height(contentStyle.profileChartSpacing))
        HorizontalBarChart(
            bars = bars,
            style = chartStyle,
            maxValue = maxValue,
            animationSpec = animationSpec,
            modifier = Modifier.fillMaxWidth(),
        )
        if (description != null) {
            Spacer(Modifier.height(contentStyle.chartDescriptionSpacing))
            BasicText(
                text = description,
                style = contentStyle.descriptionTextStyle,
                maxLines = contentStyle.descriptionMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun HorizontalBarChartCardPreview() {
    HorizontalBarChartCard(
        title = "Horizontal bar chart with border radius",
        bars = listOf(
            HorizontalBar(31f, 60f),
            HorizontalBar(56f, 80f),
            HorizontalBar(30f, 51f),
            HorizontalBar(21f, 32f),
            HorizontalBar(38f, 63f),
        ),
        name = "Andres",
        role = "Team Leader",
        avatar = { ChartInitialsAvatar(text = "A") },
        action = { ChartPillButton(text = "PROFILE", onClick = {}) },
        description = "Minim dolor in amet nulla laboris enim dolore consequat, sunt in culpa qui officia.",
        animationSpec = null,
    )
}
