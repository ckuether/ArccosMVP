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
import com.example.core_ui.resources.LocalDimensionResources
import com.example.core_ui.projection.CalculateScreenPositionFromMapUseCase
import com.example.shared.data.model.Course
import com.example.shared.data.model.Hole
import com.example.shared.data.model.Location
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
    val calculateScreenPosition: CalculateScreenPositionFromMapUseCase = koinInject()
    val dimensions = LocalDimensionResources.current
    val locationState by viewModel.locationState.collectAsStateWithLifecycle()

    val currentScoreCard by viewModel.currentScoreCard.collectAsStateWithLifecycle()
    
    // We'll use Google Maps projection directly instead of injected use case
    var mapSize by remember { mutableStateOf<IntSize?>(null) }
    var cameraPosition by remember { mutableStateOf<MapCameraPosition?>(null) }

    // Golf course and hole state
    var currentHoleNumber by remember { mutableStateOf(1) }
    var currentHole by remember { mutableStateOf<Hole?>(null) }
    var targetLocation by remember { mutableStateOf<Location?>(null) }
    var showHoleStats by remember { mutableStateOf(false) }
    var showFullScoreCard by remember { mutableStateOf(false) }
    
    // Map state for projection calculations
    var googleMapInstance by remember { mutableStateOf<Any?>(null) }
    val density = LocalDensity.current
    
    // Calculate yardage display position using Google Maps projection  
    val yardageDisplayPosition by remember(currentHole, targetLocation, googleMapInstance, mapSize, cameraPosition) {
        derivedStateOf {
            // Only calculate if camera has moved from default (0,0) position
            if (currentHole != null && targetLocation != null && googleMapInstance != null && mapSize != null &&
                cameraPosition != null && (cameraPosition!!.latitude != 0.0 || cameraPosition!!.longitude != 0.0)) {
                
                println("DEBUG YardageDisplay: All conditions met, proceeding with calculation")
                
                try {
                    val teeLocation = currentHole!!.teeLocation
                    val target = targetLocation!!
                    
                    // Calculate midpoint between tee and target
                    val midPoint = teeLocation.midPoint(target)
                    
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
    
    // UI visibility state
    var isUIVisible by remember { mutableStateOf(true) }
    var lastTouchTime by remember { mutableStateOf(getCurrentTimeMillis()) }

    // Auto-hide UI timer
    LaunchedEffect(lastTouchTime) {
        delay(TimeMillis.FIVE_SECONDS)
        println("DEBUG RoundOfGolf: Auto-hide timer expired, setting isUIVisible=false")
        isUIVisible = false
    }

    // Animation values for smooth slide transitions
    val topOffset by animateFloatAsState(
        targetValue = if (isUIVisible) 0f else -200f,
        animationSpec = tween(durationMillis = TimeMillis.HALF_SECOND.toInt()),
        label = "topOffset"
    )
    
    val bottomOffset by animateFloatAsState(
        targetValue = if (isUIVisible) 0f else 200f,
        animationSpec = tween(durationMillis = 500),
        label = "bottomOffset"
    )

    // Function to reset touch timer
    val resetUITimer = {
        lastTouchTime = getCurrentTimeMillis()
        isUIVisible = true
    }

    // Update current hole when golf course loads or hole number changes
    LaunchedEffect(golfCourse, currentHoleNumber) {
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

        // Yardage display overlay - hardcoded to 220y for now
        if (currentHole != null && targetLocation != null && yardageDisplayPosition != IntOffset.Zero) {
            YardageDisplay(
                yardage = 220,
                modifier = Modifier
                    .offset(
                        x = with(density) { yardageDisplayPosition.x.toDp() - 30.dp }, // Subtract half width to center
                        y = with(density) { yardageDisplayPosition.y.toDp() - 30.dp }  // Subtract half height to center
                    )
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

        // Bottom components row
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = bottomOffset.dp)
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingLarge)
                .padding(bottom = dimensions.paddingLarge),
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

    }
}

@Composable
private fun HoleInfoCard(
    modifier: Modifier = Modifier,
    currentHoleNumber: Int,
    currentHole: Hole?
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
                    text = currentHole?.par?.toString() ?: "---",
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
        shape = MaterialTheme.shapes.extraLarge,
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