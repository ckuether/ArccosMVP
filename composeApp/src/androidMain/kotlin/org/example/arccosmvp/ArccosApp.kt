package org.example.arccosmvp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.svg.SvgDecoder
import com.example.location.di.locationModule
import com.example.location.di.platformLocationModule
import com.example.shared.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.example.arccosmvp.di.appModule

class ArccosApp : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin
        startKoin {
            androidContext(this@ArccosApp)
            modules(locationModule, platformLocationModule, appModule, databaseModule)
        }
    }
    
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}