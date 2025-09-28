package org.example.arccosmvp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.arccosmvp.presentation.LocationTrackingViewModel
import org.example.arccosmvp.platform.MapView
import org.example.arccosmvp.platform.toMapLocation
import org.example.arccosmvp.platform.MapLocation
import org.example.arccosmvp.data.HoleRepository
import org.koin.compose.koinInject
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.arccosmvp.platform.MarkerType
import kotlin.time.ExperimentalTime
import org.example.arccosmvp.utils.DrawableHelper

@Composable
@Preview
fun App() {
    MaterialTheme {
        LocationTrackingScreen()
    }
}

@Composable
fun LocationTrackingScreen(
    viewModel: LocationTrackingViewModel = koinViewModel(),
    holeRepository: HoleRepository = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locationEvents by viewModel.locationEvents.collectAsStateWithLifecycle(initialValue = emptyList())
    
    // Load hole 1 for initial map bounds
    var initialBounds by remember { mutableStateOf<Pair<MapLocation, MapLocation>?>(null) }
    var hole1StartLocation by remember { mutableStateOf<MapLocation?>(null) }
    
    // Get golf ball icon in composable context
    val golfBallIcon = DrawableHelper.golfBall()
    
    // Load hole data on first composition
    LaunchedEffect(Unit) {
        viewModel.checkPermissionStatus()
        
        // Load hole 1 data for initial map bounds
        holeRepository.getHoleById(1)?.let { hole ->
            initialBounds = Pair(
                hole.startLocation.toMapLocation("Hole 1 Start"),
                hole.endLocation.toMapLocation("Hole 1 End")
            )
            
            // Create hole 1 start location with golf ball icon
            hole1StartLocation = MapLocation(
                latitude = hole.startLocation.lat,
                longitude = hole.startLocation.long,
                title = "Hole 1 Tee",
                icon = golfBallIcon,
                markerType = MarkerType.GOLF_BALL
            )
        }
    }
    
    Box(modifier = Modifier.fillMaxSize().safeContentPadding()) {
        // Full-screen Map
        MapView(
            modifier = Modifier.fillMaxSize(),
            locations = buildList {
                // Add location tracking points
                addAll(locationEvents.map { locationItem ->
                    locationItem.location.toMapLocation(
                        title = "Location at ${formatTimestamp(locationItem.timestamp)}"
                    )
                })
                // Add hole 1 start location with golf ball icon
                hole1StartLocation?.let { add(it) }
            },
            centerLocation = locationEvents.firstOrNull()?.location?.toMapLocation(),
            initialBounds = initialBounds
        )
        
        // Top overlay - App title and status
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp).padding(end = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Location Tracker",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "${if (uiState.isTracking) "Tracking" else "Stopped"} • ${locationEvents.size} locations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                uiState.error?.let { error ->
                    Text(
                        text = "Error: $error",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        
        // Permission overlay (when needed)
        if (uiState.hasPermission == false) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Location Permission Required",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "This app needs location permission to track your location.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = { viewModel.requestLocationPermission() },
                        enabled = !uiState.isRequestingPermission
                    ) {
                        Text(
                            if (uiState.isRequestingPermission) "Requesting..." 
                            else "Grant Permission"
                        )
                    }
                }
            }
        }
        
        // Bottom floating action buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { viewModel.startLocationTracking() },
                modifier = Modifier.weight(1f),
                containerColor = if (uiState.isTracking) 
                    MaterialTheme.colorScheme.surfaceVariant 
                else MaterialTheme.colorScheme.primary
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (uiState.isTracking) Icons.Default.LocationOn else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isTracking) "Tracking" else "Start",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            
            FloatingActionButton(
                onClick = { viewModel.stopLocationTracking() },
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stop",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            
            FloatingActionButton(
                onClick = { 
                    viewModel.clearLocations()
                    viewModel.clearError()
                },
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalTime::class)
private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    
    val months = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    
    val month = months[localDateTime.month.ordinal - 1]
    val day = localDateTime.day
    val hour = localDateTime.hour
    val minute = localDateTime.minute
    val second = localDateTime.second
    
    return "$month, $day at ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}"
}