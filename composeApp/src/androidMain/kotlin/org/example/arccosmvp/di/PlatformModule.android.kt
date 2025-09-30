package org.example.arccosmvp.di

import com.example.core_ui.platform.DrawableProvider
import org.example.arccosmvp.platform.AndroidDrawableProvider
import org.example.arccosmvp.utils.AndroidDrawableHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<AndroidDrawableHelper> { AndroidDrawableHelper(androidContext()) }
    single<DrawableProvider> { AndroidDrawableProvider(get()) }
}