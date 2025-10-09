package org.example.arccosmvp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.svg.SvgDecoder
import com.example.location.di.locationModule
import com.example.location.di.platformLocationModule
import com.example.shared.di.databaseModule
import com.example.core_ui.di.coreUIModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.example.arccosmvp.di.appModule
import org.example.arccosmvp.di.platformModule

class GolfApp : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin
        startKoin {
            androidContext(this@GolfApp)
            modules(locationModule, platformLocationModule, appModule, databaseModule, platformModule, coreUIModule)
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