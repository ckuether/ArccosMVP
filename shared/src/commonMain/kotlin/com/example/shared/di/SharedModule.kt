package com.example.shared.di

import com.example.shared.data.repository.ResourceReader
import com.example.shared.utils.ComposeResourceReader
import org.koin.dsl.module

val sharedModule = module {
    single<ResourceReader> { ComposeResourceReader() }
}