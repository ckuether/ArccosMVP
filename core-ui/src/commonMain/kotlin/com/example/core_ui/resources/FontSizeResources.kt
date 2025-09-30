package com.example.core_ui.resources

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class FontSizeResources(
    val small: TextUnit = 12.sp,
    val medium: TextUnit = 16.sp,
    val large: TextUnit = 24.sp,
)

val LocalFontSizeResources = compositionLocalOf { FontSizeResources() }