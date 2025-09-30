package org.example.arccosmvp.di

import com.example.core_ui.platform.DrawableProvider
import org.example.arccosmvp.platform.IOSDrawableProvider
import org.example.arccosmvp.utils.IOSDrawableHelper
import org.koin.dsl.module

actual val platformModule = module {
    single<IOSDrawableHelper> { IOSDrawableHelper() }
    single<DrawableProvider> { IOSDrawableProvider(get()) }
}