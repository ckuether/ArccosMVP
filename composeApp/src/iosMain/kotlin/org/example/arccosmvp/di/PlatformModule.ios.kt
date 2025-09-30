package org.example.arccosmvp.di

import com.example.core_ui.platform.DrawableProvider
import org.koin.dsl.module

// iOS-specific DrawableProvider implementation
class IOSDrawableProvider : DrawableProvider {
    override fun getGolfBallMarker(): Any? {
        // iOS doesn't use BitmapDescriptor, returns null for now
        // You can implement iOS-specific marker creation here if needed
        return null
    }
    
    override fun getGolfFlagMarker(): Any? {
        // iOS doesn't use BitmapDescriptor, returns null for now
        // You can implement iOS-specific marker creation here if needed
        return null
    }
}

actual val platformModule = module {
    single<DrawableProvider> { IOSDrawableProvider() }
}