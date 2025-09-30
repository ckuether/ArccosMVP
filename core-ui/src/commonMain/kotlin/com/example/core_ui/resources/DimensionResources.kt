package com.example.shared.resources

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DimensionResources(

    val unit: Dp = 1.dp,

    val spacingXSmall: Dp = 4.dp,
    val spacingSmall: Dp = 8.dp,
    val spacingMedium: Dp= 16.dp,
    val spacingXLarge: Dp = 32.dp,

    // Icon sizes
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,

    // Border radius
    val cornerRadiusSmall: Dp = 8.dp,
    val cornerRadiusMedium: Dp = 12.dp,
    val cornerRadiusLarge: Dp = 16.dp,

    //Elevation
    val elevationSmall: Dp = 4.dp,
    val elevationMedium: Dp = 8.dp,
)

val LocalDimensionResources = compositionLocalOf { DimensionResources() }