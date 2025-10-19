package org.example.arccosmvp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.location_domain.di.locationDomainModule
import com.example.location_domain.di.platformLocationModule
import org.example.arccosmvp.database.databaseModule
import com.example.core_ui.di.coreUIModule
import com.example.location_presentation.di.locationPresentationModule
import com.example.round_of_golf_domain.di.roundOfGolfDomainModule
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
            modules(locationDomainModule, platformLocationModule, locationPresentationModule, appModule, databaseModule, coreUIModule, roundOfGolfDomainModule)
        }
    }
}