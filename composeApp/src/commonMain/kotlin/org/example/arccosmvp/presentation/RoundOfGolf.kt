package org.example.arccosmvp.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core_ui.platform.MapView
import com.example.core_ui.platform.MapCameraPosition
import com.example.core_ui.components.YardageDisplay
import com.example.core_ui.components.YardageDisplayDefaults
import com.example.core_ui.components.TeeMarker
import com.example.core_ui.components.TeeMarkerDefaults
import com.example.core_ui.components.FlagMarker
import com.example.core_ui.components.FlagMarkerDefaults
import com.example.core_ui.components.TargetMarker
import com.example.core_ui.components.TargetMarkerDefaults
import com.example.core_ui.components.PolylineComponent
import com.example.core_ui.resources.LocalDimensionResources
import com.example.core_ui.projection.CalculateScreenPositionFromMapUseCase
import com.example.core_ui.projection.CalculateMapPositionFromScreenUseCase
import com.example.shared.data.model.Course
import com.example.shared.data.model.Player
import com.example.shared.data.model.GolfClubType
import com.example.shared.data.model.Location
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
    val calculateMapPosition: CalculateMapPositionFromScreenUseCase = koinInject()
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

    // We'll use Google Maps projection directly instead of injected use case
    var googleMapInstance by remember { mutableStateOf<Any?>(null) }
    var mapSize by remember { mutableStateOf<IntSize?>(null) }
    var cameraPosition by remember {
        val teeLocation = currentHole.teeLocation
        mutableStateOf(MapCameraPosition(teeLocation.lat, teeLocation.long, 15f))
    }

    var trackShotModeEnabled by remember { mutableStateOf(false) }
    var selectedClub by remember { mutableStateOf<GolfClubType?>(null) }

    var targetLocation by remember { mutableStateOf(currentHole.initialTarget) }

    // Calculate yardage display positions using helper function
    val yardageToTargetScreenPosition = rememberYardageScreenPosition(
        location1 = currentHole.teeLocation,
        location2 = targetLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        calculateScreenPosition = calculateScreenPosition,
        margin = 60
    )

    val yardageToTargetText by remember(currentHole, targetLocation) {
        derivedStateOf {
            currentHole.teeLocation.distanceToInYards(targetLocation)
        }
    }

    val yardageTargetToFlagScreenPosition = rememberYardageScreenPosition(
        location1 = currentHole.flagLocation,
        location2 = targetLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        calculateScreenPosition = calculateScreenPosition,
        margin = 60
    )

    val yardageTargetToFlagText by remember(currentHole, targetLocation) {
        derivedStateOf {
            currentHole.flagLocation.distanceToInYards(targetLocation)
        }
    }

    // Calculate screen positions for golf markers using helper function
    val teeMarkerScreenPosition = rememberMarkerScreenPosition(
        location = currentHole.teeLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        calculateScreenPosition = calculateScreenPosition,
        margin = 12
    )

    val flagMarkerScreenPosition = rememberMarkerScreenPosition(
        location = currentHole.flagLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        calculateScreenPosition = calculateScreenPosition,
        margin = 16
    )

    val targetMarkerScreenPosition = rememberMarkerScreenPosition(
        location = targetLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        calculateScreenPosition = calculateScreenPosition,
        margin = 20
    )

    // Calculate polyline points using helper function
    val teeToTargetPolylinePoints = rememberPolylinePoints(
        location1 = currentHole.teeLocation,
        location2 = targetLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        calculateScreenPosition = calculateScreenPosition
    )

    val targetToFlagPolylinePoints = rememberPolylinePoints(
        location1 = targetLocation,
        location2 = currentHole.flagLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        calculateScreenPosition = calculateScreenPosition
    )

    // UI visibility state
    var isUIVisible by remember { mutableStateOf(true) }
    var lastTouchTime by remember { mutableStateOf(getCurrentTimeMillis()) }

    // Consolidated dragging state
    var isDraggingMapComponent by remember { mutableStateOf(false) }
    var currentDragPosition by remember { mutableStateOf(Offset.Zero) }

    // Track which component is being dragged
    var draggedComponent by remember { mutableStateOf<DraggedComponent?>(null) }

    // Track shot mode locations (separate from hole data)
    var trackShotStartLocation by remember(currentHole) { mutableStateOf(currentHole.teeLocation) }
    var trackShotEndLocation by remember(currentHole) { mutableStateOf(currentHole.flagLocation) }


    // Calculate screen positions for track shot markers using helper function
    val trackShotStartScreenPosition = rememberMarkerScreenPosition(
        location = trackShotStartLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        calculateScreenPosition = calculateScreenPosition,
        margin = 12
    )

    val trackShotEndScreenPosition = rememberMarkerScreenPosition(
        location = trackShotEndLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        calculateScreenPosition = calculateScreenPosition,
        margin = 16
    )


    var showClubSelection by remember { mutableStateOf(false) }
    var showHoleStats by remember { mutableStateOf(false) }
    var showFullScoreCard by remember { mutableStateOf(false) }

    // Auto-hide UI timer
    LaunchedEffect(lastTouchTime) {
        delay(TimeMillis.FIVE_SECONDS)
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
            gesturesEnabled = !isDraggingMapComponent,
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
        if (!trackShotModeEnabled && teeToTargetPolylinePoints.isNotEmpty()) {
            PolylineComponent(
                points = teeToTargetPolylinePoints,
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                strokeWidth = 2f
            )
        }

        if (!trackShotModeEnabled && targetToFlagPolylinePoints.isNotEmpty()) {
            PolylineComponent(
                points = targetToFlagPolylinePoints,
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                strokeWidth = 2f
            )
        }

        if (!trackShotModeEnabled && yardageToTargetScreenPosition != IntOffset.Zero) {
            val yardageSize = YardageDisplayDefaults.getSize()
            YardageDisplay(
                yardage = yardageToTargetText,
                modifier = Modifier
                    .offset(
                        x = with(density) { yardageToTargetScreenPosition.x.toDp() - yardageSize / 2 },
                        y = with(density) { yardageToTargetScreenPosition.y.toDp() - yardageSize / 2 }
                    )
            )
        }

        if (!trackShotModeEnabled && yardageTargetToFlagScreenPosition != IntOffset.Zero) {
            val yardageSize = YardageDisplayDefaults.getSize()
            YardageDisplay(
                yardage = yardageTargetToFlagText,
                modifier = Modifier
                    .offset(
                        x = with(density) { yardageTargetToFlagScreenPosition.x.toDp() - yardageSize / 2 },
                        y = with(density) { yardageTargetToFlagScreenPosition.y.toDp() - yardageSize / 2 }
                    )
            )
        }

        // Golf Markers using screen projection
        if (!trackShotModeEnabled && teeMarkerScreenPosition != IntOffset.Zero) {
            val teeSize = TeeMarkerDefaults.getSize()
            TeeMarker(
                modifier = Modifier
                    .offset(
                        x = with(density) { teeMarkerScreenPosition.x.toDp() - teeSize / 2 },
                        y = with(density) { teeMarkerScreenPosition.y.toDp() - teeSize / 2 }
                    )
            )
        }

        if (!trackShotModeEnabled && flagMarkerScreenPosition != IntOffset.Zero) {
            val flagSize = FlagMarkerDefaults.getSize()
            FlagMarker(
                modifier = Modifier
                    .offset(
                        x = with(density) { flagMarkerScreenPosition.x.toDp() - flagSize / 2 },
                        y = with(density) { flagMarkerScreenPosition.y.toDp() - flagSize / 2 }
                    )
            )
        }


        if (!trackShotModeEnabled && targetMarkerScreenPosition != IntOffset.Zero) {
            val targetSize = TargetMarkerDefaults.getSize()

            // Calculate final position: use drag position when dragging, otherwise use calculated screen position
            val isDraggingTarget = isDraggingMapComponent && draggedComponent == DraggedComponent.TARGET
            val currentPosition = if (isDraggingTarget) currentDragPosition else targetMarkerScreenPosition.asOffset()

            val finalX = with(density) { currentPosition.x.toDp() - targetSize / 2 }
            val finalY = with(density) { currentPosition.y.toDp() - targetSize / 2 }

            TargetMarker(
                modifier = Modifier
                    .offset(x = finalX, y = finalY)
                    .dragGestures(
                        markerSize = targetSize,
                        markerScreenPosition = targetMarkerScreenPosition,
                        draggedComponent = DraggedComponent.TARGET,
                        density = density,
                        getCurrentDragState = { Pair(isDraggingMapComponent, draggedComponent) },
                        getCurrentDragPosition = { currentDragPosition },
                        onDragStart = {
                            isDraggingMapComponent = true
                            draggedComponent = DraggedComponent.TARGET
                            // Set drag position to the actual visual center coordinates
                            val visualCenterX = with(density) { finalX.toPx() + (targetSize.toPx() / 2) }
                            val visualCenterY = with(density) { finalY.toPx() + (targetSize.toPx() / 2) }
                            currentDragPosition = Offset(visualCenterX, visualCenterY)
                            resetUITimer()
                        },
                        onDragUpdate = { newPosition ->
                            currentDragPosition = newPosition
                        },
                        onDragEnd = {
                            isDraggingMapComponent = false
                            draggedComponent = null
                        },
                        onLocationUpdate = { newLocation ->
                            targetLocation = newLocation
                        },
                        googleMapInstance = googleMapInstance,
                        calculateMapPosition = calculateMapPosition,
                        mapSize = mapSize
                    )
            )
        }

        // Track Shot Mode Polyline - Yellow line connecting start to end
        if (trackShotModeEnabled && trackShotStartScreenPosition != IntOffset.Zero && trackShotEndScreenPosition != IntOffset.Zero) {
            PolylineComponent(
                points = listOf(trackShotStartScreenPosition, trackShotEndScreenPosition),
                modifier = Modifier.fillMaxSize(),
                color = Color.Yellow,
                strokeWidth = 3f
            )
        }

        // Track Shot Mode Markers - Generic markers for start and end positions
        if (trackShotModeEnabled) {
            GenericMarker(
                screenPosition = trackShotStartScreenPosition,
                color = Color.Blue,
                size = 24.dp
            )
            
            GenericMarker(
                screenPosition = trackShotEndScreenPosition,
                color = Color.Red,
                size = 24.dp
            )
        }

        // Top overlay - Hole info bar
        if (!trackShotModeEnabled) {
            HoleInfoCard(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = topOffset.dp)
                    .padding(horizontal = dimensions.paddingLarge)
                    .padding(top = dimensions.paddingLarge),
                currentHoleNumber = currentHoleNumber,
                currentHole = currentHole
            )
        }

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
        if (!trackShotModeEnabled) {
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
                            trackShotModeEnabled = true
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
                onClubSelected = { club ->
                    selectedClub = club
                    println("DEBUG: Selected club: ${club.clubName}")
                },
                onDismiss = {
                    showClubSelection = false
                }
            )
        }

        // TargetShotCard with Exit Button - shown when in track shot mode and club is selected
        if (trackShotModeEnabled && selectedClub != null) {
            BoxWithConstraints(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = topOffset.dp)
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.paddingLarge)
                    .padding(top = dimensions.paddingLarge)
            ) {
                // TargetShotCard centered
                TargetShotCard(
                    modifier = Modifier.align(Alignment.Center),
                    selectedClub = selectedClub!!,
                    distanceYards = trackShotStartLocation.distanceToInYards(trackShotEndLocation)
                )

                // Exit Button positioned to the left, centered vertically with the card
                IconButton(
                    onClick = {
                        trackShotModeEnabled = false
                        selectedClub = null
                        resetUITimer()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        )
                        .size(dimensions.iconXLarge)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit track shot mode",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

enum class DraggedComponent {
    TARGET,
    TRACKING_START,
    TRACKING_END
}

@Composable
private fun rememberMarkerScreenPosition(
    location: Location,
    googleMapInstance: Any?,
    mapSize: IntSize?,
    cameraPosition: MapCameraPosition,
    calculateScreenPosition: CalculateScreenPositionFromMapUseCase,
    margin: Int = 12
): IntOffset {
    return remember(location, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null) {
                try {
                    val screenPos = calculateScreenPosition(location, googleMapInstance)

                    screenPos?.let { pos ->
                        val clampedX = pos.x.coerceIn(margin, mapSize.width - margin)
                        val clampedY = pos.y.coerceIn(margin, mapSize.height - margin)
                        IntOffset(clampedX, clampedY)
                    } ?: IntOffset.Zero
                } catch (e: Exception) {
                    IntOffset.Zero
                }
            } else {
                IntOffset.Zero
            }
        }
    }.value
}

@Composable
private fun rememberYardageScreenPosition(
    location1: Location,
    location2: Location,
    googleMapInstance: Any?,
    mapSize: IntSize?,
    cameraPosition: MapCameraPosition,
    calculateScreenPosition: CalculateScreenPositionFromMapUseCase,
    margin: Int = 60
): IntOffset {
    return remember(location1, location2, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null) {
                try {
                    val midPoint = location1.midPoint(location2)
                    val screenPos = calculateScreenPosition(midPoint, googleMapInstance)

                    screenPos?.let { pos ->
                        val clampedX = pos.x.coerceIn(margin, mapSize.width - margin)
                        val clampedY = pos.y.coerceIn(margin, mapSize.height - margin)
                        IntOffset(clampedX, clampedY)
                    } ?: IntOffset.Zero
                } catch (e: Exception) {
                    IntOffset.Zero
                }
            } else {
                IntOffset.Zero
            }
        }
    }.value
}

@Composable
private fun rememberPolylinePoints(
    location1: Location,
    location2: Location,
    googleMapInstance: Any?,
    mapSize: IntSize?,
    cameraPosition: MapCameraPosition,
    calculateScreenPosition: CalculateScreenPositionFromMapUseCase
): List<IntOffset> {
    return remember(location1, location2, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null) {
                try {
                    val pos1 = calculateScreenPosition(location1, googleMapInstance)
                    val pos2 = calculateScreenPosition(location2, googleMapInstance)

                    if (pos1 != null && pos2 != null) {
                        listOf(
                            IntOffset(pos1.x, pos1.y),
                            IntOffset(pos2.x, pos2.y)
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
    }.value
}

private fun IntOffset.asOffset(): Offset = Offset(x.toFloat(), y.toFloat())

private fun Modifier.dragGestures(
    markerSize: Dp,
    markerScreenPosition: IntOffset,
    draggedComponent: DraggedComponent,
    density: androidx.compose.ui.unit.Density,
    getCurrentDragState: () -> Pair<Boolean, DraggedComponent?>,
    getCurrentDragPosition: () -> Offset,
    onDragStart: () -> Unit,
    onDragUpdate: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onLocationUpdate: (Location) -> Unit,
    googleMapInstance: Any?,
    calculateMapPosition: CalculateMapPositionFromScreenUseCase,
    mapSize: IntSize?
): Modifier = this.pointerInput(Unit) {
    detectDragGestures(
        onDragStart = { _ ->
            onDragStart()
        },
        onDrag = { change, dragAmount ->
            val (isDragging, currentComponent) = getCurrentDragState()
            val currentPos = getCurrentDragPosition()
            
            if (isDragging && currentComponent == draggedComponent) {
                // Update current position in screen coordinates
                val newPosition = currentPos + dragAmount

                // Clamp to map bounds
                val clampedPosition = mapSize?.let { size ->
                    val half = with(density) { markerSize.toPx() / 2 }
                    Offset(
                        newPosition.x.coerceIn(half, size.width - half),
                        newPosition.y.coerceIn(half, size.height - half)
                    )
                } ?: newPosition

                onDragUpdate(clampedPosition)

                // Convert back to map coordinates in real-time
                if (googleMapInstance != null) {
                    val cx = clampedPosition.x.toInt()
                    val cy = clampedPosition.y.toInt()
                    calculateMapPosition(
                        cx,
                        cy,
                        googleMapInstance
                    )?.let { newLocation ->
                        onLocationUpdate(newLocation)
                    }
                }

                change.consume()
            }
        },
        onDragEnd = {
            onDragEnd()
        }
    )
}