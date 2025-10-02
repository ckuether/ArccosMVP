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
    val totalScore = scoreCard.scorecard.values.filterNotNull().sum()
    val holesPlayed = scoreCard.scorecard.size
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingLarge)
        ) {
            // Round ID and basic info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Round #${scoreCard.roundId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (totalScore > 0) {
                    Text(
                        text = "Score: $totalScore",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(dimensions.spacingSmall))
            
            // Player and course info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Player ID: ${scoreCard.playerId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "$holesPlayed hole${if (holesPlayed != 1) "s" else ""} played",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Show some hole scores if available
            if (scoreCard.scorecard.isNotEmpty()) {
                Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                
                Text(
                    text = "Hole Scores:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                
                // Display scores in a grid-like format
                val scores = scoreCard.scorecard.toList().sortedBy { it.first }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
                ) {
                    items(scores) { (hole, score) ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = dimensions.paddingMedium,
                                    vertical = dimensions.paddingSmall
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "H$hole",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = score?.toString() ?: "-",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}