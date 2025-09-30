package com.example.core_ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.core_ui.resources.ColorResources
import com.example.core_ui.resources.FontSizeResources
import com.example.core_ui.resources.LocalColorResources
import com.example.core_ui.resources.LocalFontSizeResources
import com.example.core_ui.resources.Pink40
import com.example.core_ui.resources.Purple80
import com.example.core_ui.resources.DimensionResources
import com.example.core_ui.resources.LocalDimensionResources

@Composable
expect fun PlatformThemeSetup()

@Composable
fun GolfAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Create app colors based on theme
    val appColors = ColorResources()


    val colorScheme = lightColorScheme(
            primary = appColors.primary,
            secondary = appColors.textColor,
            tertiary = Pink40,
            background = appColors.offWhite,
            surface = appColors.offWhite,
            onPrimary = appColors.white,
            onSecondary = Purple80,
            onBackground = appColors.textColor,
            onSurface = appColors.textColor
    )

    PlatformThemeSetup()

    CompositionLocalProvider(
        LocalDimensionResources provides DimensionResources(),
        LocalColorResources provides appColors,
        LocalFontSizeResources provides FontSizeResources()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}