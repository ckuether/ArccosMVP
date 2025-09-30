package org.example.arccosmvp.utils

/**
 * iOS-specific helper for creating map markers
 * Currently returns null but can be extended for iOS-specific marker creation
 */
class IOSDrawableHelper {
    
    /**
     * Creates an iOS-specific marker for golf balls
     * Currently returns null as iOS MapKit uses different marker system
     */
    fun createGolfBallMarker(): Any? {
        // iOS doesn't use BitmapDescriptor, returns null for now
        // You can implement iOS-specific marker creation here if needed
        return null
    }
    
    /**
     * Creates an iOS-specific marker for golf flags
     * Currently returns null as iOS MapKit uses different marker system
     */
    fun createGolfFlagMarker(): Any? {
        // iOS doesn't use BitmapDescriptor, returns null for now
        // You can implement iOS-specific marker creation here if needed
        return null
    }
}