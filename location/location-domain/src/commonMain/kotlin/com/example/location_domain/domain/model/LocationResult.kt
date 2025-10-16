package com.example.location_domain.domain.model

import com.example.shared.data.model.Location


sealed class LocationResult {
    data class Success(val location: Location) : LocationResult()
    data class Error(val message: String) : LocationResult()
    data object PermissionDenied : LocationResult()
    data object LocationDisabled : LocationResult()
}