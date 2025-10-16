package com.example.location_domain.domain.usecase

interface CheckLocationPermissionUseCase {
    suspend operator fun invoke(): Boolean
}