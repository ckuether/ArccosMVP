package com.example.location.domain.usecase

interface RequestLocationPermissionUseCase {
    suspend operator fun invoke(): PermissionResult
}

sealed class PermissionResult {
    object Granted : PermissionResult()
    object Denied : PermissionResult()
    object PermanentlyDenied : PermissionResult()
}