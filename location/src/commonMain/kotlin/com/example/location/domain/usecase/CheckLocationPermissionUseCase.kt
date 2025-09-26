package com.example.location.domain.usecase

interface CheckLocationPermissionUseCase {
    suspend operator fun invoke(): Boolean
}