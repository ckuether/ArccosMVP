package com.example.shared.domain.model

import com.example.shared.data.event.Location

sealed class LocationResult {
    data class Success(val location: Location) : LocationResult()
    data class Error(val message: String) : LocationResult()
    data object PermissionDenied : LocationResult()
    data object LocationDisabled : LocationResult()
}