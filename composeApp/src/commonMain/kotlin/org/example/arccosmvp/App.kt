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
import com.example.shared.data.model.GolfCourse
import org.example.arccosmvp.presentation.LocationTrackingViewModel
import com.example.core_ui.platform.MapView
import com.example.core_ui.platform.toMapLocation
import com.example.core_ui.platform.MapLocation
import com.example.shared.data.repository.GolfCourseRepository
import org.koin.compose.koinInject
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.example.core_ui.platform.MarkerType
import com.example.core_ui.theme.GolfAppTheme
import kotlin.time.ExperimentalTime
import org.example.arccosmvp.utils.DrawableHelper
import com.example.shared.data.model.distanceToInYards
import org.example.arccosmvp.presentation.DraggableScoreCardBottomSheet

@Composable
@Preview
fun App() {
    GolfAppTheme {
        GolfScreen()
    }
}

@Composable
fun GolfScreen(
    viewModel: LocationTrackingViewModel = koinViewModel(),
    golfCourseRepository: GolfCourseRepository = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locationEvents by viewModel.locationEvents.collectAsStateWithLifecycle(initialValue = emptyList())
    
    // Golf course and hole state
    var golfCourse by remember { mutableStateOf<GolfCourse?>(null) }
    var currentHoleNumber by remember { mutableStateOf(1) }
    var currentHole by remember { mutableStateOf<Hole?>(null) }
    var initialBounds by remember { mutableStateOf<Pair<MapLocation, MapLocation>?>(null) }
    var holeStartLocation by remember { mutableStateOf<MapLocation?>(null) }
    var holeEndLocation by remember { mutableStateOf<MapLocation?>(null) }
    var showScoreCard by remember { mutableStateOf(false) }
    
    // Get golf ball icon in composable context
    val golfBallIcon = DrawableHelper.golfBall()
    
    // Load golf course data on first composition
    LaunchedEffect(Unit) {
        viewModel.checkPermissionStatus()
        val loadedCourse = golfCourseRepository.loadGolfCourse()
        golfCourse = loadedCourse
    }
    
    // Update current hole when golf course loads or hole number changes
    LaunchedEffect(golfCourse, currentHoleNumber) {
        golfCourse?.holes?.find { it.id == currentHoleNumber }?.let { hole ->
            currentHole = hole
            
            
            // Set up map bounds for this hole (fallback)
            initialBounds = Pair(
                hole.teeLocation.toMapLocation("Hole $currentHoleNumber Start"),
                hole.flagLocation.toMapLocation("Hole $currentHoleNumber End")
            )
            
            // Create hole start location with golf ball icon
            holeStartLocation = MapLocation(
                latitude = hole.teeLocation.lat,
                longitude = hole.teeLocation.long,
                title = "Hole $currentHoleNumber Tee",
                icon = golfBallIcon,
                markerType = MarkerType.GOLF_BALL
            )
            
            // Create hole end location with golf flag icon
            holeEndLocation = MapLocation(
                latitude = hole.flagLocation.lat,
                longitude = hole.flagLocation.long,
                title = "Hole $currentHoleNumber Pin",
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
                // Add current hole start location with golf ball icon
                holeStartLocation?.let { add(it) }
                // Add current hole end location with golf flag icon
                holeEndLocation?.let { add(it) }
            },
            centerLocation = locationEvents.firstOrNull()?.location?.toMapLocation(),
            initialBounds = initialBounds,
            currentHole = currentHole
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
                    text = currentHoleNumber.toString(),
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
                        text = currentHole?.let { hole ->
                            val distanceYards = hole.teeLocation.distanceToInYards(hole.flagLocation)
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
                        text = currentHole?.par?.toString() ?: "---",
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
            shape = MaterialTheme.shapes.extraLarge,
            onClick = { showScoreCard = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Arrow
                IconButton(
                    onClick = { 
                        if (currentHoleNumber > 1) {
                            currentHoleNumber = currentHoleNumber - 1
                        }
                    },
                    modifier = Modifier.size(32.dp),
                    enabled = currentHoleNumber > 1
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous hole",
                        tint = if (currentHoleNumber > 1) Color.Black else Color.Gray
                    )
                }
                
                // Edit Hole text and number
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Score Card Hole $currentHoleNumber",
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
                            text = currentHoleNumber.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                
                // Right Arrow
                IconButton(
                    onClick = { 
                        val maxHoles = golfCourse?.holes?.size ?: 9
                        if (currentHoleNumber < maxHoles) {
                            currentHoleNumber = currentHoleNumber + 1
                        }
                    },
                    modifier = Modifier.size(32.dp),
                    enabled = currentHoleNumber < (golfCourse?.holes?.size ?: 9)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next hole",
                        tint = if (currentHoleNumber < (golfCourse?.holes?.size ?: 9)) Color.Black else Color.Gray
                    )
                }
            }
        }
        
        // Score Card Bottom Sheet
        if (showScoreCard) {
            DraggableScoreCardBottomSheet(
                currentHole = currentHole,
                currentHoleNumber = currentHoleNumber,
                totalHoles = golfCourse?.holes?.size ?: 9,
                onDismiss = { showScoreCard = false },
                onFinishHole = { score, putts ->
                    // Handle score submission
                    println("DEBUG: Hole $currentHoleNumber finished with score: $score, putts: $putts")
                    showScoreCard = false
                },
                onNavigateToHole = { holeNumber ->
                    currentHoleNumber = holeNumber
                    showScoreCard = false
                }
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