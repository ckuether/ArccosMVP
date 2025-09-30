package org.example.arccosmvp.di

import com.example.core_ui.platform.DrawableProvider
import org.example.arccosmvp.platform.ComposeDrawableProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DrawableProvider> { ComposeDrawableProvider(androidContext()) }
}