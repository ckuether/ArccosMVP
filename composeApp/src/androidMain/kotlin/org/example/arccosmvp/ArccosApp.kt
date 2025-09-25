package org.example.arccosmvp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.example.shared.di.locationModule
import com.example.shared.di.platformLocationModule
import org.example.arccosmvp.di.appModule

class ArccosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin
        startKoin {
            androidContext(this@ArccosApp)
            modules(locationModule, platformLocationModule, appModule)
        }
    }
}