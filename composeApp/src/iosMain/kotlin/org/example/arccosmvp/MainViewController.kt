package org.example.arccosmvp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.location.di.locationModule
import com.example.location.di.platformLocationModule
import com.example.shared.di.databaseModule
import com.example.shared.di.sharedModule
import com.example.core_ui.di.coreUIModule
import org.koin.core.context.startKoin
import org.example.arccosmvp.di.appModule

fun MainViewController() = ComposeUIViewController { 
    initializeKoin()
    App() 
}

private fun initializeKoin() {
    // Check if Koin is already started to avoid reinitialization
    try {
        org.koin.mp.KoinPlatform.getKoin()
    } catch (e: Exception) {
        // Koin not started, initialize it
        startKoin {
            modules(locationModule, platformLocationModule, appModule, databaseModule, sharedModule, coreUIModule)
        }
    }
}