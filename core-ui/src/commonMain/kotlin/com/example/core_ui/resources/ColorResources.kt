package com.example.core_ui.resources

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// Material Design Colors (for theme compatibility)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// App Brand Colors
private val BrandPrimary = Purple40  // Using the darker purple for better contrast
private val BrandPrimaryDisabled = PurpleGrey80
private val BrandOffWhite = Pink80

// Base Colors
private val BaseWhite = Color(0xFFFFFFFF)
private val BaseBlack = Color(0xFF000000)

// Greys
private val Grey242 = Color(0xFF242424)
private val Grey6C7 = Color(0xFF6C6C77)

/**
 * App color palette following design system
 * Use this for consistent theming across the app
 */
data class ColorResources(
    // Brand Colors
    val primary: Color = BrandPrimary,
    val primaryDisabled: Color = BrandPrimaryDisabled,
    val offWhite: Color = BrandOffWhite,

    // Base Colors
    val white: Color = BaseWhite,
    val black: Color = BaseBlack,

    val lightGrey: Color = Color(0xFFE0E0E0),

    // Text Colors
    val textColor: Color = Grey242,
    val greyText: Color = Grey6C7,

    // Surface Colors (for backgrounds, cards, etc.)
    val surface: Color = BaseWhite,
    val surfaceVariant: Color = BrandOffWhite,

    // Status Colors (you can add these as needed)
    val error: Color = Color(0xFFE53E3E),
    val success: Color = Color(0xFF38A169),
    val warning: Color = Color(0xFFD69E2E),
    
    // Navigation drawer colors
    val transparent: Color = Color.Transparent,
    val transparentOverlay: Color = Color(0x1F000000), // 12% black overlay
    val divider: Color = lightGrey,
    val defaultButtonColor: Color = Grey6C7, // Default icon tint color


)

val LocalColorResources = compositionLocalOf { ColorResources() }