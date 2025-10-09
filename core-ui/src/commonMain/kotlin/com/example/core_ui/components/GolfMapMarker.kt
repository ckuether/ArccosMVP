package com.example.core_ui.components

import androidx.compose.runtime.Composable
import com.example.shared.data.model.Location
import com.example.core_ui.platform.MarkerType

// For Android - returns a Composable marker
@Composable
expect fun GolfMapMarker(
    type: MarkerType,
    location: Location,
    onLocationChanged: ((Location) -> Unit)? = null
)

// For iOS - returns a platform-specific marker object  
expect fun createGolfMapMarker(
    type: MarkerType,
    location: Location,
): Any