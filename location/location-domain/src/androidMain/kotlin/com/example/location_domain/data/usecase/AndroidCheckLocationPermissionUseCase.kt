package com.example.location_domain.data.usecase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.location_domain.domain.usecase.CheckLocationPermissionUseCase

class AndroidCheckLocationPermissionUseCase(
    private val context: Context
) : CheckLocationPermissionUseCase {
    
    override suspend fun invoke(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}