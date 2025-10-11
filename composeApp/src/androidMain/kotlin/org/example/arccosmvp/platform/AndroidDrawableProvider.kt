package org.example.arccosmvp.platform

import com.example.core_ui.platform.DrawableProvider
import org.example.arccosmvp.utils.AndroidDrawableHelper

/**
 * ComposeApp-specific implementation of DrawableProvider.
 * This provides access to the app's drawable resources for the core-ui module.
 */
class AndroidDrawableProvider(private val drawableHelper: AndroidDrawableHelper) : DrawableProvider {

    override fun getGolfBallMarker(): Any? {
        return drawableHelper.createGolfBallMarker()
    }

    override fun getGolfFlagMarker(): Any? {
        return drawableHelper.createGolfFlagMarker()
    }

    override fun getTargetCircleMarker(): Any? {
        return drawableHelper.createTargetCircleMarker()
    }
}