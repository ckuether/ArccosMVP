package com.example.core_ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.toArgb
import com.example.core_ui.resources.LocalColorResources

@Composable
actual fun PlatformThemeSetup() {
    val appColors = LocalColorResources.current
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Set status bar color using modern approach
            window.statusBarColor = appColors.primary.toArgb()

            // Enable edge-to-edge display (modern approach)
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Set status bar appearance using modern API
            val windowInsetsController = WindowCompat.getInsetsController(window, view)

            // Set status bar icons to white (since orange background is dark)
            windowInsetsController.isAppearanceLightStatusBars = false

            // Set navigation bar icons to dark (if you want to handle nav bar too)
            windowInsetsController.isAppearanceLightNavigationBars = true
        }
    }
}