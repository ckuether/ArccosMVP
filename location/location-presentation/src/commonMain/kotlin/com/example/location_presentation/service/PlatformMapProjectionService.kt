package com.example.location_presentation.service

import com.example.location_domain.domain.service.MapProjectionService

/**
 * Platform-specific factory for creating MapProjectionService implementations.
 * This allows each platform to provide its own implementation (Google Maps on Android, MapKit on iOS, etc.)
 */
expect class PlatformMapProjectionService() : MapProjectionService