package org.example.arccosmvp.platform

import com.example.core_ui.platform.DrawableProvider
import org.example.arccosmvp.utils.IOSDrawableHelper

/**
 * iOS-specific implementation of DrawableProvider.
 * This provides access to iOS drawable resources for the core-ui module.
 */
class IOSDrawableProvider(private val drawableHelper: IOSDrawableHelper) : DrawableProvider {
    
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