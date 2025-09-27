package org.example.arccosmvp

import android.app.Application
import com.example.location.di.locationModule
import com.example.location.di.platformLocationModule
import com.example.shared.data.database.DatabaseBuilder
import com.example.shared.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.example.arccosmvp.di.appModule

class ArccosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize database
        DatabaseBuilder.initialize(this)
        
        // Initialize Koin
        startKoin {
            androidContext(this@ArccosApp)
            modules(locationModule, platformLocationModule, appModule, databaseModule)
        }
    }
}