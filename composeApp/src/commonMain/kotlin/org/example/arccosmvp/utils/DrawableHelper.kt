package org.example.arccosmvp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import arccosmvp.composeapp.generated.resources.Res
import arccosmvp.composeapp.generated.resources.golf_ball
import arccosmvp.composeapp.generated.resources.golf_bg
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Centralized drawable resources factory for consistent resource usage across the app
 * Follows the Factory pattern for resource creation
 */
object DrawableHelper {

    /**
     * Golf ball icon painter
     * Used for marking tee areas and golf-related locations on maps
     * Uses Coil for SVG support on Android, falls back to painterResource on other platforms
     */
    @Composable
    fun golfBall(): Painter {
        return getSvgPainter(Res.drawable.golf_ball)
    }

    /**
     * Golf background image painter
     * Used as background image for golf-related screens
     */
    @Composable
    fun golfBackground(): Painter {
        return getPainterRes(Res.drawable.golf_bg)
    }

    /**
     * Generic function to get painter from SVG drawable resource
     * Uses Coil's AsyncImagePainter for SVG support on Android
     * @param resource The DrawableResource to convert to Painter
     * @return Painter instance for the given resource
     */
    @Composable
    private fun getSvgPainter(resource: DrawableResource): Painter {
        return rememberAsyncImagePainter(model = resource)
    }


    /**
     * Generic function to get painter from drawable resource (non-SVG)
     * @param resource The DrawableResource to convert to Painter
     * @return Painter instance for the given resource
     */
    @Composable
    private fun getPainterRes(resource: DrawableResource): Painter {
        return painterResource(resource)
    }
}