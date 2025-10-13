package org.example.arccosmvp.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core_ui.platform.MapView
import com.example.core_ui.platform.MapCameraPosition
import com.example.core_ui.components.YardageDisplay
import com.example.core_ui.components.TeeMarker
import com.example.core_ui.components.FlagMarker
import com.example.core_ui.components.TargetMarker
import com.example.core_ui.components.PolylineComponent
import com.example.core_ui.resources.LocalDimensionResources
import com.example.core_ui.projection.CalculateScreenPositionFromMapUseCase
import com.example.shared.data.model.Course
import com.example.shared.data.model.Hole
import com.example.shared.data.model.Player
import com.example.shared.data.model.distanceToInYards
import com.example.shared.data.model.midPoint
import com.example.shared.platform.getCurrentTimeMillis
import com.example.shared.utils.TimeMillis
import org.example.arccosmvp.presentation.viewmodel.RoundOfGolfViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun RoundOfGolf(
    currentPlayer: Player,
    golfCourse: Course,
    viewModel: RoundOfGolfViewModel = koinViewModel { parametersOf(golfCourse) }
) {

    val density = LocalDensity.current
    val dimensions = LocalDimensionResources.current

    val calculateScreenPosition: CalculateScreenPositionFromMapUseCase = koinInject()
    val locationState by viewModel.locationState.collectAsStateWithLifecycle()

    val currentScoreCard by viewModel.currentScoreCard.collectAsStateWithLifecycle()

    // Golf course and hole state
    var currentHoleNumber by remember { mutableStateOf(1) }
    var currentHole by remember {
        mutableStateOf(
            golfCourse.holes.find { it.id == currentHoleNumber } 
                ?: golfCourse.holes.first()
        )
    }
    var targetLocation by remember { mutableStateOf(currentHole.initialTarget) }

    // We'll use Google Maps projection directly instead of injected use case
    var googleMapInstance by remember { mutableStateOf<Any?>(null) }
    var mapSize by remember { mutableStateOf<IntSize?>(null) }
    var cameraPosition by remember {
        val teeLocation = currentHole.teeLocation
        mutableStateOf(MapCameraPosition(teeLocation.lat, teeLocation.long, 15f))
    }

    var showHoleStats by remember { mutableStateOf(false) }
    var showFullScoreCard by remember { mutableStateOf(false) }
    
    // Calculate yardage display position using Google Maps projection  
    val yardageToTargetScreenPosition by remember(currentHole, targetLocation, mapSize, cameraPosition) {
        derivedStateOf {
            // Only calculate if camera has moved from default (0,0) position
            if (googleMapInstance != null && mapSize != null) {
                println("DEBUG YardageDisplay: All conditions met, proceeding with calculation")
                try {
                    val teeLocation = currentHole.teeLocation
                    
                    // Calculate midpoint between tee and target
                    val midPoint = teeLocation.midPoint(targetLocation)
                    
                    // Use Google Maps SDK projection for accurate positioning
                    val screenPos = calculateScreenPosition(midPoint, googleMapInstance!!)
                    
                    screenPos?.let { pos ->
                        // Ensure the position is within screen bounds
                        val clampedX = pos.x.coerceIn(60, mapSize!!.width - 60) // Leave 60px margin
                        val clampedY = pos.y.coerceIn(60, mapSize!!.height - 60) // Leave 60px margin
                        val result = IntOffset(clampedX, clampedY)
                        println("DEBUG YardageDisplay: Final result: x=${result.x}, y=${result.y}")
                        result
                    } ?: run {
                        println("DEBUG YardageDisplay: screenPos was null, returning IntOffset.Zero")
                        IntOffset.Zero
                    }
                } catch (e: Exception) {
                    println("DEBUG YardageDisplay: Exception in yardage positioning: ${e.message}")
                    e.printStackTrace()
                    IntOffset.Zero
                }
            } else {
                println("DEBUG YardageDisplay: Conditions not met, returning IntOffset.Zero")
                IntOffset.Zero
            }
        }
    }

    val yardageToTargetText by remember(currentHole, targetLocation) {
        derivedStateOf {
            currentHole.teeLocation.distanceToInYards(targetLocation)
        }
    }

    val yardageTargetToFlagScreenPosition by remember(currentHole, targetLocation, mapSize, cameraPosition) {
        derivedStateOf {
            // Only calculate if camera has moved from default (0,0) position
            if (googleMapInstance != null && mapSize != null) {
                try {
                    val flagLocation = currentHole.flagLocation

                    // Calculate midpoint between tee and target
                    val midPoint = flagLocation.midPoint(targetLocation)

                    // Use Google Maps SDK projection for accurate positioning
                    val screenPos = calculateScreenPosition(midPoint, googleMapInstance!!)

                    screenPos?.let { pos ->
                        // Ensure the position is within screen bounds
                        val clampedX = pos.x.coerceIn(60, mapSize!!.width - 60) // Leave 60px margin
                        val clampedY = pos.y.coerceIn(60, mapSize!!.height - 60) // Leave 60px margin
                        val result = IntOffset(clampedX, clampedY)
                        result
                    } ?: run {
                        println("DEBUG YardageDisplay: screenPos was null, returning IntOffset.Zero")
                        IntOffset.Zero
                    }
                } catch (e: Exception) {
                    println("DEBUG YardageDisplay: Exception in yardage positioning: ${e.message}")
                    e.printStackTrace()
                    IntOffset.Zero
                }
            } else {
                println("DEBUG YardageDisplay: Conditions not met, returning IntOffset.Zero")
                IntOffset.Zero
            }
        }
    }

    val yardageTargetToFlagText by remember(currentHole, targetLocation) {
        derivedStateOf {
            currentHole.flagLocation.distanceToInYards(targetLocation)
        }
    }

    // Calculate screen positions for golf markers using Google Maps projection
    val teeMarkerScreenPosition by remember(currentHole, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null) {
                try {
                    val teeLocation = currentHole.teeLocation
                    val screenPos = calculateScreenPosition(teeLocation, googleMapInstance!!)
                    
                    screenPos?.let { pos ->
                        val clampedX = pos.x.coerceIn(12, mapSize!!.width - 12) // Leave margin for marker size
                        val clampedY = pos.y.coerceIn(12, mapSize!!.height - 12)
                        IntOffset(clampedX, clampedY)
                    } ?: IntOffset.Zero
                } catch (e: Exception) {
                    IntOffset.Zero
                }
            } else {
                IntOffset.Zero
            }
        }
    }

    val flagMarkerScreenPosition by remember(currentHole, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null) {
                try {
                    val flagLocation = currentHole.flagLocation
                    val screenPos = calculateScreenPosition(flagLocation, googleMapInstance!!)
                    
                    screenPos?.let { pos ->
                        val clampedX = pos.x.coerceIn(16, mapSize!!.width - 16) // Leave margin for marker size
                        val clampedY = pos.y.coerceIn(16, mapSize!!.height - 16)
                        IntOffset(clampedX, clampedY)
                    } ?: IntOffset.Zero
                } catch (e: Exception) {
                    IntOffset.Zero
                }
            } else {
                IntOffset.Zero
            }
        }
    }

    val targetMarkerScreenPosition by remember(targetLocation, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null) {
                try {
                    val screenPos = calculateScreenPosition(targetLocation, googleMapInstance!!)
                    
                    screenPos?.let { pos ->
                        val clampedX = pos.x.coerceIn(20, mapSize!!.width - 20) // Leave margin for marker size
                        val clampedY = pos.y.coerceIn(20, mapSize!!.height - 20)
                        IntOffset(clampedX, clampedY)
                    } ?: IntOffset.Zero
                } catch (e: Exception) {
                    IntOffset.Zero
                }
            } else {
                IntOffset.Zero
            }
        }
    }

    // Calculate polyline points using screen projection
    val teeToTargetPolylinePoints by remember(currentHole, targetLocation, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null && currentHole != null && targetLocation != null) {
                try {
                    val teeScreenPos = calculateScreenPosition(currentHole.teeLocation, googleMapInstance!!)
                    val targetScreenPos = calculateScreenPosition(targetLocation, googleMapInstance!!)
                    
                    if (teeScreenPos != null && targetScreenPos != null) {
                        listOf(
                            IntOffset(teeScreenPos.x, teeScreenPos.y),
                            IntOffset(targetScreenPos.x, targetScreenPos.y)
                        )
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    val targetToFlagPolylinePoints by remember(currentHole, targetLocation, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null && currentHole != null && targetLocation != null) {
                try {
                    val targetScreenPos = calculateScreenPosition(targetLocation, googleMapInstance!!)
                    val flagScreenPos = calculateScreenPosition(currentHole.flagLocation, googleMapInstance!!)
                    
                    if (targetScreenPos != null && flagScreenPos != null) {
                        listOf(
                            IntOffset(targetScreenPos.x, targetScreenPos.y),
                            IntOffset(flagScreenPos.x, flagScreenPos.y)
                        )
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }
    
    // UI visibility state
    var isUIVisible by remember { mutableStateOf(true) }
    var lastTouchTime by remember { mutableStateOf(getCurrentTimeMillis()) }
    var showClubSelection by remember { mutableStateOf(false) }

    // Auto-hide UI timer
    LaunchedEffect(lastTouchTime) {
        delay(TimeMillis.FIVE_SECONDS)
        println("DEBUG RoundOfGolf: Auto-hide timer expired, setting isUIVisible=false")
        isUIVisible = false
    }

    // Animation values for smooth slide transitions
    val topOffset by animateFloatAsState(
        targetValue = if (isUIVisible) 0f else -200f,
        animationSpec = tween(durationMillis = TimeMillis.ANIMATION_DEFAULT.toInt()),
        label = "topOffset"
    )
    
    val bottomOffset by animateFloatAsState(
        targetValue = if (isUIVisible) 0f else 200f,
        animationSpec = tween(durationMillis = TimeMillis.ANIMATION_DEFAULT.toInt()),
        label = "bottomOffset"
    )

    // Function to reset touch timer
    val resetUITimer = {
        lastTouchTime = getCurrentTimeMillis()
        isUIVisible = true
    }

    // Update current hole when golf course loads or hole number changes
    LaunchedEffect(currentHoleNumber) {
        golfCourse.holes.find { it.id == currentHoleNumber }?.let { hole ->
            currentHole = hole
            targetLocation = hole.initialTarget
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { resetUITimer() }
    ) {
        // Full-screen Map
        MapView(
            modifier = Modifier.fillMaxSize(),
            currentHole = currentHole,
            targetLocation = targetLocation,
            hasLocationPermission = locationState.hasPermission == true,
            onMapClick = { mapLocation ->
                resetUITimer()
                // Always set/replace target shot at clicked location
            },
            onTargetLocationChanged = { newLocation ->
                targetLocation = newLocation
            },
            onMapSizeChanged = { width, height ->
                mapSize = IntSize(width, height)
            },
            onCameraPositionChanged = { newCameraPosition ->
                cameraPosition = newCameraPosition
            },
            onMapReady = { mapInstance ->
                googleMapInstance = mapInstance
            }
        )

        // Polylines using screen projection
        if (teeToTargetPolylinePoints.isNotEmpty()) {
            PolylineComponent(
                points = teeToTargetPolylinePoints,
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                strokeWidth = 2f
            )
        }

        if (targetToFlagPolylinePoints.isNotEmpty()) {
            PolylineComponent(
                points = targetToFlagPolylinePoints,
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                strokeWidth = 2f
            )
        }

        if (yardageToTargetScreenPosition != IntOffset.Zero) {
            YardageDisplay(
                yardage = yardageToTargetText,
                modifier = Modifier
                    .offset(
                        x = with(density) { yardageToTargetScreenPosition.x.toDp() - 30.dp }, // Subtract half width to center
                        y = with(density) { yardageToTargetScreenPosition.y.toDp() - 30.dp }  // Subtract half height to center
                    )
            )
        }

        if (yardageTargetToFlagScreenPosition != IntOffset.Zero) {
            YardageDisplay(
                yardage = yardageTargetToFlagText,
                modifier = Modifier
                    .offset(
                        x = with(density) { yardageTargetToFlagScreenPosition.x.toDp() - 30.dp }, // Subtract half width to center
                        y = with(density) { yardageTargetToFlagScreenPosition.y.toDp() - 30.dp }  // Subtract half height to center
                    )
            )
        }

        // Golf Markers using screen projection
        if (teeMarkerScreenPosition != IntOffset.Zero) {
            TeeMarker(
                modifier = Modifier
                    .offset(
                        x = with(density) { teeMarkerScreenPosition.x.toDp() - (dimensions.iconMedium / 2) },
                        y = with(density) { teeMarkerScreenPosition.y.toDp() - (dimensions.iconMedium / 2) }
                    )
            )
        }

        if (flagMarkerScreenPosition != IntOffset.Zero) {
            FlagMarker(
                modifier = Modifier
                    .offset(
                        x = with(density) { flagMarkerScreenPosition.x.toDp() - (dimensions.iconSmall / 2) },
                        y = with(density) { flagMarkerScreenPosition.y.toDp() - (dimensions.iconSmall / 2) }
                    )
            )
        }

        if (targetMarkerScreenPosition != IntOffset.Zero) {
            TargetMarker(
                modifier = Modifier
                    .offset(
                        x = with(density) { targetMarkerScreenPosition.x.toDp() - (dimensions.iconXLarge / 2) },
                        y = with(density) { targetMarkerScreenPosition.y.toDp() - (dimensions.iconXLarge / 2) }
                    ),
                onClick = {
                    // Allow moving the target by clicking on it
                    resetUITimer()
                }
            )
        }

        // Top overlay - Hole info bar
        HoleInfoCard(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = topOffset.dp)
                .padding(horizontal = dimensions.paddingLarge)
                .padding(top = dimensions.paddingLarge),
            currentHoleNumber = currentHoleNumber,
            currentHole = currentHole
        )

        // Permission overlay (when needed)
        if (locationState.hasPermission == false) {
            LocationPermissionCard(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(dimensions.paddingLarge),
                isRequestingPermission = locationState.isRequestingPermission,
                onRequestPermission = { viewModel.requestLocationPermission() }
            )
        }

        // Bottom components column
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = bottomOffset.dp)
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingLarge)
                .padding(bottom = dimensions.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
        ) {
            // Track Shot button - same layout as bottom row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium),
                verticalAlignment = Alignment.Bottom
            ) {
                // Empty space to match MiniScorecard width
                Spacer(modifier = Modifier.width(72.dp)) // Approximate width of MiniScorecard
                
                // Track Shot button - fills available space like HoleNavigationCard
                TrackShotCard(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        resetUITimer()
                        showClubSelection = true
                    }
                )

                // Empty space to match the spacer on the right
                Spacer(modifier = Modifier.width(dimensions.spacingXXLarge))
            }

            // Bottom components row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium),
                verticalAlignment = Alignment.Bottom
            ) {
                // To Par Scorecard - Bottom Left
                MiniScorecard(
                    scoreToPar = viewModel.getScoreToPar(),
                    onScoreCardClick = { 
                        resetUITimer()
                        showFullScoreCard = true 
                    }
                )

                // Edit Hole component - fills available space
                HoleNavigationCard(
                    modifier = Modifier.weight(1f),
                    currentHoleNumber = currentHoleNumber,
                    maxHoles = golfCourse.holes.size,
                    onPreviousHole = {
                        resetUITimer()
                        if (currentHoleNumber > 1) {
                            currentHoleNumber = currentHoleNumber - 1
                        }
                    },
                    onNextHole = {
                        resetUITimer()
                        val maxHoles = golfCourse.holes.size
                        if (currentHoleNumber < maxHoles) {
                            currentHoleNumber = currentHoleNumber + 1
                        }
                    },
                    onClick = { 
                        resetUITimer()
                        showHoleStats = true
                    }
                )

                Spacer(modifier = Modifier.width(dimensions.spacingXXLarge))
            }
        }

        // Score Card Bottom Sheet
        if (showHoleStats) {
            HoleStatsBottomSheet(
                currentHole = currentHole,
                currentHoleNumber = currentHoleNumber,
                totalHoles = golfCourse.holes.size,
                existingScore = viewModel.getHoleScore(currentHoleNumber),
                onDismiss = { showHoleStats = false },
                onFinishHole = { score, putts ->
                    // Handle score submission
                    viewModel.updateHoleScore(currentHoleNumber, score)
                    println("DEBUG: Hole $currentHoleNumber finished with score: $score, putts: $putts")
                    showHoleStats = false

                    // Navigate to next hole (equivalent to hitting next button)
                    val maxHoles = golfCourse.holes.size
                    if (currentHoleNumber < maxHoles) {
                        currentHoleNumber = currentHoleNumber + 1
                    }
                },
                onNavigateToHole = { holeNumber ->
                    currentHoleNumber = holeNumber
                    showHoleStats = false
                }
            )
        }

        // Full ScoreCard Bottom Sheet
        if (showFullScoreCard) {
            ScoreCardBottomSheet(
                course = golfCourse,
                currentPlayer = currentPlayer,
                currentScoreCard = currentScoreCard,
                onDismiss = { showFullScoreCard = false }
            )
        }

        // Club Selection Dialog
        if (showClubSelection) {
            ClubSelectionDialog(
                onClubSelected = { selectedClub ->
                    // TODO: Implement track shot functionality with selected club
                    println("DEBUG: Selected club: ${selectedClub.clubName}")
                },
                onDismiss = { showClubSelection = false }
            )
        }

    }
}

@Composable
private fun HoleInfoCard(
    modifier: Modifier = Modifier,
    currentHoleNumber: Int,
    currentHole: Hole
) {
    val dimensions = LocalDimensionResources.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(
            modifier = Modifier.padding(horizontal = dimensions.paddingXLarge, vertical = dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingXLarge)
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
                    .height(dimensions.spacingXXLarge)
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
                    text = "${currentHole.teeLocation.distanceToInYards(currentHole.flagLocation)}yds",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Vertical Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(dimensions.spacingXXLarge)
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
                    text = currentHole.par.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@Composable
private fun LocationPermissionCard(
    modifier: Modifier = Modifier,
    isRequestingPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    val dimensions = LocalDimensionResources.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensions.paddingLarge),
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
                modifier = Modifier.padding(vertical = dimensions.paddingSmall)
            )
            Button(
                onClick = onRequestPermission,
                enabled = !isRequestingPermission
            ) {
                Text(
                    if (isRequestingPermission) "Requesting..."
                    else "Grant Permission"
                )
            }
        }
    }
}

@Composable
private fun TrackShotCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensionResources.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensions.paddingLarge,
                    vertical = dimensions.paddingMedium
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Track Shot",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun HoleNavigationCard(
    modifier: Modifier = Modifier,
    currentHoleNumber: Int,
    maxHoles: Int,
    onPreviousHole: () -> Unit,
    onNextHole: () -> Unit,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensionResources.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensions.paddingLarge,
                    vertical = dimensions.paddingMedium
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Arrow
            IconButton(
                onClick = onPreviousHole,
                modifier = Modifier.size(dimensions.iconButtonSize),
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
                    text = "Hole $currentHoleNumber",
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
                        modifier = Modifier.padding(
                            horizontal = dimensions.paddingLarge,
                            vertical = dimensions.paddingSmall
                        )
                    )
                }
            }

            // Right Arrow
            IconButton(
                onClick = onNextHole,
                modifier = Modifier.size(dimensions.iconButtonSize),
                enabled = currentHoleNumber < maxHoles
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next hole",
                    tint = if (currentHoleNumber < maxHoles) Color.Black else Color.Gray
                )
            }
        }
    }
}