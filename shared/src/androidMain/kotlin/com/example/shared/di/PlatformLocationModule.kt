package com.example.shared.di

import com.example.shared.data.repository.AndroidLocationProvider
import com.example.shared.data.repository.LocationProvider
import org.koin.dsl.module

actual val platformLocationModule = module {
    single<LocationProvider> { AndroidLocationProvider(get()) }
}