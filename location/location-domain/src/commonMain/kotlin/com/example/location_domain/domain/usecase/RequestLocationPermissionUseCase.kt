package com.example.location_domain.domain.usecase

interface RequestLocationPermissionUseCase {
    suspend operator fun invoke(): PermissionResult
}

sealed class PermissionResult {
    object Granted : PermissionResult()
    object Denied : PermissionResult()
    object PermanentlyDenied : PermissionResult()
}