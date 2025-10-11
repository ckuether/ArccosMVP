package com.example.core_ui.platform

/**
 * Interface for providing platform-specific drawable resources to the core-ui module.
 * This allows core-ui to remain independent of specific resource implementations.
 */
interface DrawableProvider {
    /**
     * Provides a BitmapDescriptor for golf ball markers
     */
    fun getGolfBallMarker(): Any?

    /**
     * Provides a BitmapDescriptor for golf flag markers
     */
    fun getGolfFlagMarker(): Any?

    /**
     * Provides a BitmapDescriptor for target circle markers
     */
    fun getTargetCircleMarker(): Any?
}