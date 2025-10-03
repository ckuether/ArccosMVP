package org.example.arccosmvp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core_ui.platform.MapLocation
import com.example.core_ui.platform.MapView
import com.example.core_ui.platform.MarkerType
import com.example.core_ui.platform.toMapLocation
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.data.model.Hole
import com.example.shared.data.model.distanceToInYards
import org.example.arccosmvp.presentation.viewmodel.RoundOfGolfViewModel
import org.example.arccosmvp.utils.DrawableHelper
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RoundOfGolf(
    viewModel: RoundOfGolfViewModel = koinViewModel()
) {
    val dimensions = LocalDimensionResources.current
    val locationState by viewModel.locationState.collectAsStateWithLifecycle()
    val golfCourse by viewModel.golfCourse.collectAsStateWithLifecycle()
    val currentPlayer by viewModel.currentPlayer.collectAsStateWithLifecycle()
    val currentScoreCard by viewModel.currentScoreCard.collectAsStateWithLifecycle()

    // Golf course and hole state
    var currentHoleNumber by remember { mutableStateOf(1) }
    var currentHole by remember { mutableStateOf<Hole?>(null) }
    var initialBounds by remember { mutableStateOf<Pair<MapLocation, MapLocation>?>(null) }
    var holeStartLocation by remember { mutableStateOf<MapLocation?>(null) }
    var holeEndLocation by remember { mutableStateOf<MapLocation?>(null) }
    var showScoreCard by remember { mutableStateOf(false) }
    var showFullScoreCard by remember { mutableStateOf(false) }

    // Get golf ball icon in composable context
    val golfBallIcon = DrawableHelper.golfBall()


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
            userLocations = buildList {
                // Only show hole markers, no location tracking points
                // Add current hole start location with golf ball icon
                holeStartLocation?.let { add(it) }
                // Add current hole end location with golf flag icon
                holeEndLocation?.let { add(it) }
            },
            centerLocation = null,
            initialBounds = initialBounds, // Only center when hole changes
            currentHole = currentHole,
            hasLocationPermission = locationState.hasPermission == true
        )

        // Top overlay - Hole info bar
        HoleInfoCard(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
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
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingLarge)
                .padding(bottom = dimensions.paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium),
            verticalAlignment = Alignment.Bottom
        ) {
            // To Par Scorecard - Bottom Left
            MiniScorecard(
                scoreToPar = viewModel.getScoreToPar(),
                onScoreCardClick = { showFullScoreCard = true }
            )

            // Edit Hole component - fills available space
            HoleNavigationCard(
                modifier = Modifier.weight(1f),
                currentHoleNumber = currentHoleNumber,
                maxHoles = golfCourse?.holes?.size ?: 9,
                onPreviousHole = {
                    if (currentHoleNumber > 1) {
                        currentHoleNumber = currentHoleNumber - 1
                    }
                },
                onNextHole = {
                    val maxHoles = golfCourse?.holes?.size ?: 9
                    if (currentHoleNumber < maxHoles) {
                        currentHoleNumber = currentHoleNumber + 1
                    }
                },
                onClick = { showScoreCard = true }
            )

            Spacer(modifier = Modifier.width(dimensions.spacingXXLarge))
        }

        // Score Card Bottom Sheet
        if (showScoreCard) {
            HoleStatsBottomSheet(
                currentHole = currentHole,
                currentHoleNumber = currentHoleNumber,
                totalHoles = golfCourse?.holes?.size ?: 9,
                existingScore = viewModel.getHoleScore(currentHoleNumber),
                onDismiss = { showScoreCard = false },
                onFinishHole = { score, putts ->
                    // Handle score submission
                    viewModel.updateHoleScore(currentHoleNumber, score)
                    println("DEBUG: Hole $currentHoleNumber finished with score: $score, putts: $putts")
                    showScoreCard = false

                    // Navigate to next hole (equivalent to hitting next button)
                    val maxHoles = golfCourse?.holes?.size ?: 9
                    if (currentHoleNumber < maxHoles) {
                        currentHoleNumber = currentHoleNumber + 1
                    }
                },
                onNavigateToHole = { holeNumber ->
                    currentHoleNumber = holeNumber
                    showScoreCard = false
                }
            )
        }

        // Full ScoreCard Bottom Sheet
        if (showFullScoreCard) {
            ScoreCardBottomSheet(
                golfCourse = golfCourse,
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