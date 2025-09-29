package org.example.arccosmvp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shared.data.model.Hole
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
import com.example.shared.data.model.distanceToInYards

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
    var hole1EndLocation by remember { mutableStateOf<MapLocation?>(null) }
    var hole1Data by remember { mutableStateOf<Hole?>(null) }
    
    // Get golf ball icon in composable context
    val golfBallIcon = DrawableHelper.golfBall()
    
    // Load hole data on first composition
    LaunchedEffect(Unit) {
        viewModel.checkPermissionStatus()
        
        // Load hole 1 data for initial map bounds
        holeRepository.getHoleById(1)?.let { hole ->
            hole1Data = hole
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
            
            // Create hole 1 end location with golf flag icon
            hole1EndLocation = MapLocation(
                latitude = hole.endLocation.lat,
                longitude = hole.endLocation.long,
                title = "Hole 1 Pin",
                icon = golfBallIcon, // Will use different marker type
                markerType = MarkerType.GOLF_FLAG
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
                // Add hole 1 end location with golf flag icon
                hole1EndLocation?.let { add(it) }
            },
            centerLocation = locationEvents.firstOrNull()?.location?.toMapLocation(),
            initialBounds = initialBounds
        )
        
        // Top overlay - Hole info bar
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
            ),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Hole Number
                Text(
                    text = "1",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                )
                
                // Distance to Hole
                Column {
                    Text(
                        text = "Mid Green",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = hole1Data?.let { hole ->
                            val distanceYards = hole.startLocation.distanceToInYards(hole.endLocation)
                            "${distanceYards.toInt()}yds"
                        } ?: "---yds",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                )
                
                // Par
                Column {
                    Text(
                        text = "Par",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = hole1Data?.par?.toString() ?: "4",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
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
        
        // Edit Hole component
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.85f)
            ),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Arrow
                IconButton(
                    onClick = { /* Handle previous hole */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous hole",
                        tint = Color.Black
                    )
                }
                
                // Edit Hole text and number
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Score Card Hole 1",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black
                    )
                    
                    // Hole number box
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.1f)
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "1",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                
                // Right Arrow
                IconButton(
                    onClick = { /* Handle next hole */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next hole",
                        tint = Color.Black
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