package org.example.arccosmvp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.example.core_ui.platform.DrawableProvider

/**
 * App-specific implementation of DrawableProvider.
 * This provides access to the app's drawable resources for the core-ui module.
 */
class AppDrawableProvider : DrawableProvider {

    @Composable
    override fun getGolfBallPainter(): Painter {
        return DrawableHelper.golfBall()
    }
}