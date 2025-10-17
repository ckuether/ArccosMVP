package com.example.round_of_golf_presentation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core_ui.components.DraggableBottomSheetWrapper
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.data.model.Course
import com.example.shared.data.model.Player
import com.example.shared.data.model.ScoreCard

@Composable
fun ScoreCardBottomSheet(
    course: Course?,
    currentPlayer: Player?,
    currentScoreCard: ScoreCard?,
    onDismiss: () -> Unit
) {
    DraggableBottomSheetWrapper(
        onDismiss = onDismiss,
        fillMaxHeight = null // Let it wrap content
    ) {
        ScoreCardContent(
            course = course,
            currentPlayer = currentPlayer,
            currentScoreCard = currentScoreCard
        )
    }
}

@Composable
private fun ScoreCardContent(
    course: Course?,
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
                        text = course?.name ?: "Golf Course",
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
                        val allHoles = course?.holes ?: (1..18).map { null }
                        
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

@Composable
fun ScoreButton(
    score: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    par: Int = 4
) {
    val dimensions = LocalDimensionResources.current
    val scoreName = getScoreName(score, par)

    if (isSelected) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(dimensions.scoreButtonSize)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = score.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (scoreName.isNotEmpty()) {
                        Text(
                            text = scoreName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .size(dimensions.scoreButtonSize)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.5f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ParButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    par: Int = 4
) {
    val dimensions = LocalDimensionResources.current
    if (isSelected) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(dimensions.scoreButtonSize)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = par.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Par",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(dimensions.scoreButtonSize)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = par.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(dimensions.paddingXSmall))
            Text(
                text = "Par",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun PuttsButton(
    putts: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensionResources.current
    Box(
        modifier = Modifier
            .size(dimensions.puttsButtonSize)
            .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(dimensions.cornerRadiusSmall)
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = putts,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getScoreName(score: Int, par: Int): String {
    if (score == 1) return "Hole In One"
    return when (score - par) {
        -2 -> "Eagle"
        -1 -> "Birdie"
        0 -> "Par"
        1 -> "Bogey"
        else -> {
            if (score - par > 1) "+${score - par}"
            else ""
        }
    }
}