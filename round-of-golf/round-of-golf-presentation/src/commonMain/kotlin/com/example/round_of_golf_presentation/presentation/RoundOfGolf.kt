package com.example.round_of_golf_presentation.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GolfCourse
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.location_presentation.platform.MapView
import com.example.location_presentation.platform.MapCameraPosition
import com.example.core_ui.components.FloatingActionButton
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.utils.StringResources
import com.example.core_ui.utils.UiEvent
import com.example.core_ui.utils.UiText
import com.example.location_domain.domain.model.ScreenPoint
import com.example.location_domain.domain.service.MapProjectionService
import com.example.round_of_golf_domain.domain.usecase.TrackSingleRoundEventUseCase
import com.example.round_of_golf_presentation.presentation.components.DraggableMarker
import com.example.round_of_golf_presentation.presentation.components.HoleInfoCard
import com.example.round_of_golf_presentation.presentation.components.HoleNavigationCard
import com.example.round_of_golf_presentation.presentation.components.LocationPermissionCard
import com.example.round_of_golf_presentation.presentation.components.TargetShotCard
import com.example.round_of_golf_presentation.presentation.components.TrackShotCard
import com.example.shared.data.model.Course
import com.example.shared.data.model.Player
import com.example.shared.data.model.GolfClubType
import com.example.shared.data.model.Location
import com.example.shared.data.model.distanceToInYards
import com.example.shared.data.model.midPoint
import com.example.round_of_golf_domain.data.model.RoundOfGolfEvent
import com.example.shared.platform.getCurrentTimeMillis
import com.example.round_of_golf_presentation.RoundOfGolfViewModel
import com.example.round_of_golf_presentation.presentation.components.FlagMarker
import com.example.round_of_golf_presentation.presentation.components.FlagMarkerDefaults
import com.example.round_of_golf_presentation.presentation.components.MiniScorecard
import com.example.round_of_golf_presentation.presentation.components.PolylineComponent
import com.example.round_of_golf_presentation.presentation.components.TargetMarker
import com.example.round_of_golf_presentation.presentation.components.TargetMarkerDefaults
import com.example.round_of_golf_presentation.presentation.components.TeeMarker
import com.example.round_of_golf_presentation.presentation.components.TeeMarkerDefaults
import com.example.round_of_golf_presentation.presentation.components.YardageDisplay
import com.example.round_of_golf_presentation.presentation.components.YardageDisplayDefaults
import com.example.shared.utils.TimeMillis
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RoundOfGolf(
    currentPlayer: Player,
    golfCourse: Course,
    updateUiEvent: (UiEvent) -> Unit,
    viewModel: RoundOfGolfViewModel = koinViewModel { parametersOf(golfCourse, currentPlayer) }
) {

    val density = LocalDensity.current
    val dimensions = LocalDimensionResources.current
    val coroutineScope = rememberCoroutineScope()

    val mapProjectionService: MapProjectionService = koinInject()
    val trackEventUseCase: TrackSingleRoundEventUseCase = koinInject()
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
        mapProjectionService = mapProjectionService,
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
        mapProjectionService = mapProjectionService,
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
        mapProjectionService = mapProjectionService,
        margin = 12
    )

    val flagMarkerScreenPosition = rememberMarkerScreenPosition(
        location = currentHole.flagLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        mapProjectionService = mapProjectionService,
        margin = 16
    )

    val targetMarkerScreenPosition = rememberMarkerScreenPosition(
        location = targetLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        mapProjectionService = mapProjectionService,
        margin = 20
    )

    // Calculate polyline points using helper function
    val teeToTargetPolylinePoints = rememberPolylinePoints(
        location1 = currentHole.teeLocation,
        location2 = targetLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        mapProjectionService = mapProjectionService
    )

    val targetToFlagPolylinePoints = rememberPolylinePoints(
        location1 = targetLocation,
        location2 = currentHole.flagLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        mapProjectionService = mapProjectionService
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
    var trackShotEndLocation by remember(currentHole) { mutableStateOf(currentHole.initialTarget) }


    // Calculate screen positions for track shot markers using helper function
    val trackShotStartScreenPosition = rememberMarkerScreenPosition(
        location = trackShotStartLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        mapProjectionService = mapProjectionService,
        margin = 12
    )

    val trackShotEndScreenPosition = rememberMarkerScreenPosition(
        location = trackShotEndLocation,
        googleMapInstance = googleMapInstance,
        mapSize = mapSize,
        cameraPosition = cameraPosition,
        mapProjectionService = mapProjectionService,
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
        targetValue = if (isUIVisible) 0f else 400f,
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
            onMapClick = { location ->
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
        if (teeMarkerScreenPosition != IntOffset.Zero) {
            val teeSize = TeeMarkerDefaults.getSize()
            TeeMarker(
                modifier = Modifier
                    .offset(
                        x = with(density) { teeMarkerScreenPosition.x.toDp() - teeSize / 2 },
                        y = with(density) { teeMarkerScreenPosition.y.toDp() - teeSize / 2 }
                    )
            )
        }

        if (flagMarkerScreenPosition != IntOffset.Zero) {
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

            // Calculate final position using helper function
            val (targetX, targetY) = calculateDraggableMarkerPosition(
                markerSize = targetSize,
                isDraggingMapComponent = isDraggingMapComponent,
                draggedComponent = draggedComponent,
                targetDraggedComponent = DraggedComponent.TARGET,
                currentDragPosition = currentDragPosition,
                defaultScreenPosition = targetMarkerScreenPosition,
                density = density
            )

            TargetMarker(
                modifier = Modifier
                    .offset(x = targetX, y = targetY)
                    .dragGestures(
                        markerSize = targetSize,
                        draggedComponent = DraggedComponent.TARGET,
                        density = density,
                        getCurrentDragState = { Pair(isDraggingMapComponent, draggedComponent) },
                        getCurrentDragPosition = { currentDragPosition },
                        onDragStart = {
                            isDraggingMapComponent = true
                            draggedComponent = DraggedComponent.TARGET
                            // Set drag position to the actual visual center coordinates
                            val visualCenterX =
                                with(density) { targetX.toPx() + (targetSize.toPx() / 2) }
                            val visualCenterY =
                                with(density) { targetY.toPx() + (targetSize.toPx() / 2) }
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
                        mapProjectionService = mapProjectionService,
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
            val markerSize = 36.dp

            // Calculate tracking start marker position using helper function
            val (trackingStartX, trackingStartY) = calculateDraggableMarkerPosition(
                markerSize = markerSize,
                isDraggingMapComponent = isDraggingMapComponent,
                draggedComponent = draggedComponent,
                targetDraggedComponent = DraggedComponent.TRACKING_START,
                currentDragPosition = currentDragPosition,
                defaultScreenPosition = trackShotStartScreenPosition,
                density = density
            )

            DraggableMarker(
                modifier = Modifier
                    .offset(x = trackingStartX, y = trackingStartY)
                    .dragGestures(
                        markerSize = markerSize,
                        draggedComponent = DraggedComponent.TRACKING_START,
                        density = density,
                        getCurrentDragState = { Pair(isDraggingMapComponent, draggedComponent) },
                        getCurrentDragPosition = { currentDragPosition },
                        onDragStart = {
                            isDraggingMapComponent = true
                            draggedComponent = DraggedComponent.TRACKING_START
                            // Set drag position to the actual visual center coordinates
                            val visualCenterX = with(density) { trackingStartX.toPx() + (markerSize.toPx() / 2) }
                            val visualCenterY = with(density) { trackingStartY.toPx() + (markerSize.toPx() / 2) }
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
                            trackShotStartLocation = newLocation
                        },
                        googleMapInstance = googleMapInstance,
                        mapProjectionService = mapProjectionService,
                        mapSize = mapSize
                    ),
                color = Color.Blue,
                size = markerSize
            )

            val (trackingEndX, trackingEndY) = calculateDraggableMarkerPosition(
                markerSize = markerSize,
                isDraggingMapComponent = isDraggingMapComponent,
                draggedComponent = draggedComponent,
                targetDraggedComponent = DraggedComponent.TRACKING_END,
                currentDragPosition = currentDragPosition,
                defaultScreenPosition = trackShotEndScreenPosition,
                density = density
            )

            DraggableMarker(
                modifier = Modifier
                    .offset(x = trackingEndX, y = trackingEndY)
                    .dragGestures(
                        markerSize = markerSize,
                        draggedComponent = DraggedComponent.TRACKING_END,
                        density = density,
                        getCurrentDragState = { Pair(isDraggingMapComponent, draggedComponent) },
                        getCurrentDragPosition = { currentDragPosition },
                        onDragStart = {
                            isDraggingMapComponent = true
                            draggedComponent = DraggedComponent.TRACKING_END
                            // Set drag position to the actual visual center coordinates
                            val visualCenterX = with(density) { trackingEndX.toPx() + (markerSize.toPx() / 2) }
                            val visualCenterY = with(density) { trackingEndY.toPx() + (markerSize.toPx() / 2) }
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
                            trackShotEndLocation = newLocation
                        },
                        googleMapInstance = googleMapInstance,
                        mapProjectionService = mapProjectionService,
                        mapSize = mapSize
                    ),
                color = Color.Red,
                size = markerSize
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
                    // Only apply safe content padding when UI is visible to allow full off-screen animation
                    .then(if (isUIVisible) Modifier.safeContentPadding() else Modifier)
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
                                // Track hole navigation event
                                coroutineScope.launch {
                                    try {
                                        trackEventUseCase.execute(
                                            event = RoundOfGolfEvent.PreviousHole(),
                                            roundId = currentScoreCard.roundId,
                                            playerId = currentPlayer.id,
                                            holeNumber = currentHoleNumber
                                        )
                                    } catch (e: Exception) {
                                        println("DEBUG: Failed to track previous hole event: ${e.message}")
                                    }
                                }
                            }
                        },
                        onNextHole = {
                            resetUITimer()
                            val maxHoles = golfCourse.holes.size
                            if (currentHoleNumber < maxHoles) {
                                currentHoleNumber = currentHoleNumber + 1
                                // Track hole navigation event
                                coroutineScope.launch {
                                    try {
                                        trackEventUseCase.execute(
                                            event = RoundOfGolfEvent.NextHole(),
                                            roundId = currentScoreCard.roundId,
                                            playerId = currentPlayer.id,
                                            holeNumber = currentHoleNumber
                                        )
                                    } catch (e: Exception) {
                                        println("DEBUG: Failed to track next hole event: ${e.message}")
                                    }
                                }
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

        // This was the last hole - finish the round
        val roundCompletedMessage = UiText.StringResourceId(StringResources.roundCompleted).asString()

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
                        // Track next hole event
                        coroutineScope.launch {
                            try {
                                trackEventUseCase.execute(
                                    event = RoundOfGolfEvent.NextHole(),
                                    roundId = currentScoreCard.roundId,
                                    playerId = currentPlayer.id,
                                    holeNumber = currentHoleNumber
                                )
                            } catch (e: Exception) {
                                println("DEBUG: Failed to track next hole event after finishing hole: ${e.message}")
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            try {
                                trackEventUseCase.execute(
                                    event = RoundOfGolfEvent.FinishRound(),
                                    roundId = currentScoreCard.roundId,
                                    playerId = currentPlayer.id,
                                    holeNumber = currentHoleNumber
                                )

                                updateUiEvent(UiEvent.ShowSnackbar(UiText.DynamicString(roundCompletedMessage)))
                            } catch (e: Exception) {
                                println("DEBUG: Failed to track finish round event: ${e.message}")
                            }
                        }
                    }
                },
                onNavigateToHole = { holeNumber ->
                    val previousHole = currentHoleNumber
                    currentHoleNumber = holeNumber
                    showHoleStats = false
                    
                    // Track navigation event based on direction
                    coroutineScope.launch {
                        try {
                            val event = if (holeNumber > previousHole) {
                                RoundOfGolfEvent.NextHole()
                            } else {
                                RoundOfGolfEvent.PreviousHole()
                            }
                            
                            trackEventUseCase.execute(
                                event = event,
                                roundId = currentScoreCard.roundId,
                                playerId = currentPlayer.id,
                                holeNumber = holeNumber
                            )
                        } catch (e: Exception) {
                            println("DEBUG: Failed to track hole navigation event: ${e.message}")
                        }
                    }
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
                FloatingActionButton(
                    onClick = {
                        trackShotModeEnabled = false
                        selectedClub = null
                        resetUITimer()
                    },
                    icon = Icons.Default.Close,
                    contentDescription = UiText.StringResourceId(StringResources.exitTrackShotMode).asString(),
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }

        // Bottom action buttons layout
        if(trackShotModeEnabled) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = bottomOffset.dp)
                    // Only apply safe content padding when UI is visible to allow full off-screen animation
                    .then(if (isUIVisible) Modifier.safeContentPadding() else Modifier)
                    .fillMaxWidth()
                    .padding(dimensions.paddingLarge),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
            ) {
                // Empty space (same width as golf button)
                Spacer(modifier = Modifier.width(dimensions.iconXXLarge))

                val shotTrackedStr = UiText.StringResourceId(StringResources.shotTrackedTemplate, arrayOf(currentHoleNumber)).asString()
                
                // Track Shot button (fills remaining space)
                TrackShotCard(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        // Track shot event
                        coroutineScope.launch {
                            try {
                                val shotEvent = RoundOfGolfEvent.ShotTracked(
                                    holeNumber = currentHoleNumber
                                )
                                
                                trackEventUseCase.execute(
                                    event = shotEvent,
                                    roundId = currentScoreCard.roundId,
                                    playerId = currentPlayer.id,
                                    holeNumber = currentHoleNumber
                                )

                                updateUiEvent(UiEvent.ShowSnackbar(UiText.DynamicString(shotTrackedStr)))
                            } catch (e: Exception) {
                                val errorMessage = "Failed to track shot: ${e.message ?: "Unknown error"}"
                                updateUiEvent(UiEvent.ShowErrorSnackbar(UiText.DynamicString(errorMessage)))
                            }
                        }
                        resetUITimer()
                    }
                )
                
                // Club Selection Button (anchored to bottom right)
                FloatingActionButton(
                    onClick = {
                        showClubSelection = true
                        resetUITimer()
                    },
                    icon = Icons.Default.GolfCourse,
                    contentDescription = UiText.StringResourceId(StringResources.selectGolfClub).asString(),
                    size = dimensions.iconXXLarge,
                    iconSize = 28.dp
                )
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
    mapProjectionService: MapProjectionService,
    margin: Int = 12
): IntOffset {
    return remember(location, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null) {
                try {
                    val screenCoordinates = mapProjectionService.mapToScreenCoordinates(location, googleMapInstance)
                    val screenPoint = screenCoordinates?.let { ScreenPoint(it.x, it.y) }

                    screenPoint?.let { point ->
                        val clampedX = point.x.coerceIn(margin, mapSize.width - margin)
                        val clampedY = point.y.coerceIn(margin, mapSize.height - margin)
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
    mapProjectionService: MapProjectionService,
    margin: Int = 60
): IntOffset {
    return remember(location1, location2, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null) {
                try {
                    val midPoint = location1.midPoint(location2)
                    val screenCoordinates = mapProjectionService.mapToScreenCoordinates(midPoint, googleMapInstance)
                    val screenPoint = screenCoordinates?.let { ScreenPoint(it.x, it.y) }

                    screenPoint?.let { point ->
                        val clampedX = point.x.coerceIn(margin, mapSize.width - margin)
                        val clampedY = point.y.coerceIn(margin, mapSize.height - margin)
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
    mapProjectionService: MapProjectionService
): List<IntOffset> {
    return remember(location1, location2, mapSize, cameraPosition) {
        derivedStateOf {
            if (googleMapInstance != null && mapSize != null) {
                try {
                    val screenCoordinates1 = mapProjectionService.mapToScreenCoordinates(location1, googleMapInstance)
                    val screenCoordinates2 = mapProjectionService.mapToScreenCoordinates(location2, googleMapInstance)

                    if (screenCoordinates1 != null && screenCoordinates2 != null) {
                        listOf(
                            IntOffset(screenCoordinates1.x, screenCoordinates1.y),
                            IntOffset(screenCoordinates2.x, screenCoordinates2.y)
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

@Composable
private fun calculateDraggableMarkerPosition(
    markerSize: Dp,
    isDraggingMapComponent: Boolean,
    draggedComponent: DraggedComponent?,
    targetDraggedComponent: DraggedComponent,
    currentDragPosition: Offset,
    defaultScreenPosition: IntOffset,
    density: androidx.compose.ui.unit.Density
): Pair<Dp, Dp> {
    val isDraggingThisComponent = isDraggingMapComponent && draggedComponent == targetDraggedComponent
    val position = if (isDraggingThisComponent) currentDragPosition else defaultScreenPosition.asOffset()
    
    val finalX = with(density) { position.x.toDp() - markerSize / 2 }
    val finalY = with(density) { position.y.toDp() - markerSize / 2 }
    
    return Pair(finalX, finalY)
}

private fun Modifier.dragGestures(
    markerSize: Dp,
    draggedComponent: DraggedComponent,
    density: androidx.compose.ui.unit.Density,
    getCurrentDragState: () -> Pair<Boolean, DraggedComponent?>,
    getCurrentDragPosition: () -> Offset,
    onDragStart: () -> Unit,
    onDragUpdate: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onLocationUpdate: (Location) -> Unit,
    googleMapInstance: Any?,
    mapProjectionService: MapProjectionService,
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
                    mapProjectionService.screenToMapCoordinates(
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