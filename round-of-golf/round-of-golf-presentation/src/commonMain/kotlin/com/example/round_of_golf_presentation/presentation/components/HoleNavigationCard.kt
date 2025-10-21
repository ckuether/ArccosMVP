package com.example.round_of_golf_presentation.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.example.core_ui.resources.LocalDimensionResources
import com.example.core_ui.strings.StringResourcesManager
import org.koin.compose.koinInject

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
    val stringManager: StringResourcesManager = koinInject()

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
                    contentDescription = stringManager.getPreviousHole(),
                    tint = if (currentHoleNumber > 1) Color.Black else Color.Gray
                )
            }

            // Edit Hole text and number
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringManager.getHole(currentHoleNumber),
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
                    contentDescription = stringManager.getNextHole(),
                    tint = if (currentHoleNumber < maxHoles) Color.Black else Color.Gray
                )
            }
        }
    }
}