package com.example.core_ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.example.shared.data.model.Location
import com.example.shared.data.model.Hole

data class MapLocation(
    val latitude: Double,
    val longitude: Double,
    val title: String? = null,
    val icon: Painter? = null,
    val markerType: MarkerType = MarkerType.DEFAULT
)

enum class MarkerType {
    DEFAULT,
    GOLF_BALL,
    GOLF_FLAG,
    TARGET_CIRCLE
}

@Composable
expect fun MapView(
    modifier: Modifier = Modifier,
    currentHole: Hole?,
    hasLocationPermission: Boolean,
    onMapClick: ((MapLocation) -> Unit)? = null
)

// Extension function to convert our Location to MapLocation
fun Location.toMapLocation(title: String? = null): MapLocation {
    return MapLocation(
        latitude = lat,
        longitude = long,
        title = title
    )
}