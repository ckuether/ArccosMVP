package org.example.arccosmvp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.svg.SvgDecoder
import com.example.location_domain.di.locationDomainModule
import com.example.location_domain.di.platformLocationModule
import com.example.shared.di.databaseModule
import com.example.shared.di.sharedModule
import com.example.core_ui.di.coreUIModule
import com.example.location_presentation.di.locationPresentationModule
import com.example.round_of_golf_domain.di.roundOfGolfDomainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.example.arccosmvp.di.appModule

class GolfApp : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin
        startKoin {
            androidContext(this@GolfApp)
            modules(appModule, databaseModule, sharedModule, coreUIModule, locationDomainModule, locationPresentationModule, platformLocationModule, roundOfGolfDomainModule)
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