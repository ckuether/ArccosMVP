package org.example.arccosmvp.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.shared.event.Location

data class MapLocation(
    val latitude: Double,
    val longitude: Double,
    val title: String? = null
)

@Composable
expect fun MapView(
    modifier: Modifier = Modifier,
    locations: List<MapLocation> = emptyList(),
    centerLocation: MapLocation? = null,
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