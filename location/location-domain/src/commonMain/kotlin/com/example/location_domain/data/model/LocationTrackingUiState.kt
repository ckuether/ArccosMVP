package com.example.location_domain.data.model

data class LocationTrackingUiState(
    val isLoading: Boolean = false,
    val isTracking: Boolean = false,
    val hasPermission: Boolean? = null, // null = unknown, true = granted, false = denied
    val isRequestingPermission: Boolean = false,
    val error: String? = null
)