package org.example.arccosmvp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core_ui.components.DraggableBottomSheetWrapper
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.data.model.ScoreCard

@Composable
fun PreviousRoundsBottomSheet(
    scoreCards: List<ScoreCard>,
    onDismiss: () -> Unit
) {
    DraggableBottomSheetWrapper(
        onDismiss = onDismiss,
        fillMaxHeight = 0.8f
    ) {
        PreviousRoundsContent(
            scoreCards = scoreCards
        )
    }
}

@Composable
private fun PreviousRoundsContent(
    scoreCards: List<ScoreCard>
) {
    val dimensions = LocalDimensionResources.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingXXLarge)
    ) {
        // Title
        Text(
            text = "Previous Rounds",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = dimensions.spacingLarge)
        )
        
        if (scoreCards.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No previous rounds found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                    Text(
                        text = "Your completed rounds will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Scorecards list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
            ) {
                items(scoreCards) { scoreCard ->
                    ScoreCardItem(scoreCard = scoreCard)
                }
            }
        }
    }
}

@Composable
private fun ScoreCardItem(
    scoreCard: ScoreCard
) {
    val dimensions = LocalDimensionResources.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingLarge)
        ) {
            // Course name and view course button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Broken Tee Golf Course", // Placeholder course name
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = { /* View course action */ }) {
                    Text(
                        text = "View Course",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Course details
            Text(
                text = "Broken Tee Englewood • ${scoreCard.holesPlayed} Holes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
            
            // To Par score with purple background and Final label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Final (Thru ${scoreCard.holesPlayed} Holes)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(dimensions.spacingSmall))
            
            // To Par score display
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "To Par",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.width(dimensions.spacingMedium))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.size(width = 50.dp, height = 40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = if (scoreCard.toPar > 0) "+${scoreCard.toPar}" else "${scoreCard.toPar}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(dimensions.spacingLarge))
                
                // Gross score
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gross: ${scoreCard.totalScore}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
            
            // Statistics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(label = "Par", count = scoreCard.pars)
                StatisticItem(label = "Birdies", count = scoreCard.birdies)
                StatisticItem(label = "Bogeys", count = scoreCard.bogeys)
            }
            
            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
            
            // Date
            Text(
                text = "09/12/2025", // Placeholder date
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatisticItem(
    label: String,
    count: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}