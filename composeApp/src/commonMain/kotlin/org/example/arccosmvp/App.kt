package org.example.arccosmvp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.arccosmvp.presentation.LocationItem
import org.example.arccosmvp.presentation.LocationTrackingViewModel
import org.example.arccosmvp.platform.MapView
import org.example.arccosmvp.platform.toMapLocation
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@Composable
@Preview
fun App() {
    MaterialTheme {
        LocationTrackingScreen()
    }
}

@Composable
fun LocationTrackingScreen(
    viewModel: LocationTrackingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locationEvents by viewModel.locationEvents.collectAsStateWithLifecycle(initialValue = emptyList())
    
    // Check permission status on first composition
    LaunchedEffect(Unit) {
        viewModel.checkPermissionStatus()
    }
    
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header
        Text(
            text = "Location Tracker",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Permission Section
        if (uiState.hasPermission == false) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
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
                        onClick = {
                            viewModel.requestLocationPermission() },
                        enabled = !uiState.isRequestingPermission,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            if (uiState.isRequestingPermission) "Requesting..." 
                            else "Grant Permission"
                        )
                    }
                }
            }
        }
        
        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.startLocationTracking() },
                enabled = !uiState.isTracking && uiState.hasPermission == true,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (uiState.isTracking) "Tracking..." else "Start Tracking")
            }
            
            Button(
                onClick = { viewModel.stopLocationTracking() },
                enabled = uiState.isTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text("Stop")
            }
            
            OutlinedButton(
                onClick = { viewModel.clearLocations() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }
        
        // Status
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Permission: ${when (uiState.hasPermission) {
                        true -> "Granted"
                        false -> "Denied" 
                        null -> "Checking..."
                    }}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Status: ${if (uiState.isTracking) "Tracking" else "Stopped"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total locations: ${locationEvents.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                uiState.error?.let { error ->
                    Text(
                        text = "Error: $error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    TextButton(
                        onClick = { viewModel.clearError() }
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
        
        // Map View
        if (locationEvents.isNotEmpty()) {
            Text(
                text = "Location Map",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 16.dp)
            ) {
                MapView(
                    modifier = Modifier.fillMaxSize(),
                    locations = locationEvents.map { locationItem ->
                        locationItem.location.toMapLocation(
                            title = "Location at ${formatTimestamp(locationItem.timestamp)}"
                        )
                    },
                    centerLocation = locationEvents.firstOrNull()?.location?.toMapLocation()
                )
            }
        }
        
        // Location List
        if (locationEvents.isNotEmpty()) {
            Text(
                text = "Location History",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(locationEvents) { locationItem ->
                    LocationItemCard(locationItem)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No locations tracked yet.\nTap 'Start Tracking' to begin.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LocationItemCard(locationItem: LocationItem) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Coordinates",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Lat: ${locationItem.location.lat}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Lng: ${locationItem.location.long}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                //TODO: Format Time as Oct, 25 at 22:10:15
                text = "Time: ${formatTimestamp(locationItem.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
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