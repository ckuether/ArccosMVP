package org.example.arccosmvp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.platform.LocalDensity
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.data.model.GolfClubType
import com.example.shared.data.model.Hole
import com.example.shared.data.model.distanceToInYards


@Composable
fun HoleInfoCard(
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
fun LocationPermissionCard(
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
fun TrackShotCard(
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
fun HoleNavigationCard(
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

@Composable
fun TargetShotCard(
    modifier: Modifier = Modifier,
    selectedClub: GolfClubType,
    distanceYards: Int
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
            // Selected Club
            Column {
                Text(
                    text = "Club",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedClub.shortName,
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

            // Distance to Hole
            Column {
                Text(
                    text = "Distance",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${distanceYards}yds",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun DraggableMarker(
    modifier: Modifier = Modifier,
    color: Color,
    size: Dp = 36.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = color,
                shape = CircleShape
            )
            .background(
                color = Color.White,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.7f)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )
    }
}
