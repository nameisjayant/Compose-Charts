package com.nameisjayant.charts.bar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * One named set of bar values, drawn in a single [color].
 *
 * [label] is what the legend shows. Values are magnitudes: negative and NaN values are drawn as 0.
 */
@Immutable
public data class BarSeries(
    val label: String,
    val values: List<Float>,
    val color: Color,
)
