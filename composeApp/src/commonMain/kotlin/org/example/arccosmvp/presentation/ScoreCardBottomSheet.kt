package org.example.arccosmvp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core_ui.components.DraggableBottomSheetWrapper
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.data.model.GolfCourse
import com.example.shared.data.model.Player
import com.example.shared.data.model.ScoreCard

@Composable
fun ScoreCardBottomSheet(
    golfCourse: GolfCourse?,
    currentPlayer: Player?,
    currentScoreCard: ScoreCard?,
    onDismiss: () -> Unit
) {
    DraggableBottomSheetWrapper(
        onDismiss = onDismiss,
        fillMaxHeight = null // Let it wrap content
    ) {
        ScoreCardContent(
            golfCourse = golfCourse,
            currentPlayer = currentPlayer,
            currentScoreCard = currentScoreCard
        )
    }
}

@Composable
private fun ScoreCardContent(
    golfCourse: GolfCourse?,
    currentPlayer: Player?,
    currentScoreCard: ScoreCard?
) {
    val dimensions = LocalDimensionResources.current
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = dimensions.paddingLarge),
        verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
    ) {
            item {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensions.paddingMedium)
                ) {
                    Text(
                        text = golfCourse?.name ?: "Golf Course",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Blue Tee",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                // Scorecard Table
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = dimensions.spacingXXLarge),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Gray.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
                ) {
                    Column(
                        modifier = Modifier.padding(dimensions.paddingLarge)
                    ) {
                        val scrollState = rememberScrollState()
                        val allHoles = golfCourse?.holes ?: (1..18).map { null }
                        
                        // Calculate total par
                        var totalPar = 0
                        allHoles.forEach { hole ->
                            val par = hole?.par ?: 4
                            totalPar += par
                        }
                        
                        // Calculate total score
                        var totalScore = 0
                        (1..allHoles.size).forEach { holeNumber ->
                            val score = currentScoreCard?.scorecard?.get(holeNumber)
                            if (score != null) totalScore += score
                        }
                        
                        // Table Header
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Fixed column header
                            Text(
                                text = "Hole",
                                modifier = Modifier.width(60.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                            
                            // Scrollable columns
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(scrollState),
                                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                            ) {
                                // Hole numbers
                                allHoles.forEachIndexed { index, _ ->
                                    Text(
                                        text = (index + 1).toString(),
                                        modifier = Modifier.width(45.dp),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = Color.Gray
                                    )
                                }
                                
                                // Total header
                                Text(
                                    text = "Total",
                                    modifier = Modifier.width(60.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

                        // Par row
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Fixed column
                            Text(
                                text = "Par",
                                modifier = Modifier.width(60.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                            
                            // Scrollable columns
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(scrollState),
                                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                            ) {
                                // Par values
                                allHoles.forEach { hole ->
                                    val par = hole?.par ?: 4
                                    Text(
                                        text = par.toString(),
                                        modifier = Modifier.width(45.dp),
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center,
                                        color = Color.Black
                                    )
                                }
                                
                                // Total par
                                Text(
                                    text = totalPar.toString(),
                                    modifier = Modifier.width(60.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black
                                )
                            }
                        }

                        // Player score row
                        currentPlayer?.let { player ->
                            Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                            
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Fixed column
                                Text(
                                    text = player.name.take(8), // Truncate long names
                                    modifier = Modifier.width(60.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black,
                                    maxLines = 1
                                )
                                
                                // Scrollable columns
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .horizontalScroll(scrollState),
                                    horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                                ) {
                                    // Player scores
                                    allHoles.forEachIndexed { index, _ ->
                                        val holeNumber = index + 1
                                        val score = currentScoreCard?.scorecard?.get(holeNumber)
                                        Text(
                                            text = score?.toString() ?: "-",
                                            modifier = Modifier.width(45.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            textAlign = TextAlign.Center,
                                            color = Color.Black
                                        )
                                    }
                                    
                                    // Total score
                                    Text(
                                        text = if (totalScore > 0) totalScore.toString() else "-",
                                        modifier = Modifier.width(60.dp),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

        item {
            Spacer(modifier = Modifier.height(dimensions.paddingMedium))
        }
    }
}