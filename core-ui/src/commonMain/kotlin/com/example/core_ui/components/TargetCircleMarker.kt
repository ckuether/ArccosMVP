package com.example.core_ui.components

/**
 * Creates a target circle asset for the given platform
 * Returns Bitmap on Android, UIImage on iOS
 */
expect fun createTargetCircleAsset(sizeDp: Float): Any?