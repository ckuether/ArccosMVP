package com.example.core_ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Interface for providing platform-specific drawable resources to the core-ui module.
 * This allows core-ui to remain independent of specific resource implementations.
 */
interface DrawableProvider {
    /**
     * Provides a Painter for golf ball markers
     */
    @Composable
    fun getGolfBallPainter(): Painter
}