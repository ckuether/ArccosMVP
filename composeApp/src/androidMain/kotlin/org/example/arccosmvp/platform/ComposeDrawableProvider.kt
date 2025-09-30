package org.example.arccosmvp.platform

import android.content.Context
import com.example.core_ui.platform.DrawableProvider
import org.example.arccosmvp.utils.AndroidDrawableHelper

/**
 * ComposeApp-specific implementation of DrawableProvider.
 * This provides access to the app's drawable resources for the core-ui module.
 */
class ComposeDrawableProvider(private val context: Context) : DrawableProvider {
    
    override fun getGolfBallMarker(): Any? {
        return AndroidDrawableHelper.createGolfBallMarker(context)
    }
    
    override fun getGolfFlagMarker(): Any? {
        return AndroidDrawableHelper.createGolfFlagMarker(context)
    }
}