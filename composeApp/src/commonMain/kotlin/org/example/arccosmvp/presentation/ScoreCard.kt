package org.example.arccosmvp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.data.model.Hole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreCardBottomSheet(
    currentHole: Hole?,
    currentHoleNumber: Int,
    totalHoles: Int,
    onDismiss: () -> Unit,
    onFinishHole: (score: Int, putts: Int) -> Unit,
    onNavigateToHole: (holeNumber: Int) -> Unit
) {
    var selectedScore by remember(currentHoleNumber) { mutableStateOf<Int?>(null) }
    var selectedPutts by remember(currentHoleNumber) { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // Score section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Score",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Others",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Score selection grid
        val par = currentHole?.par ?: 4
        val scores = (1..9).toList()
        
        // First row: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            scores.take(3).forEach { score ->
                ScoreButton(
                    score = score,
                    isSelected = selectedScore == score,
                    onClick = { selectedScore = score },
                    par = par
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Second row: 4 (Par), 5 (Bogey highlighted), 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            scores.drop(3).take(3).forEach { score ->
                when {
                    score == par -> ParButton(
                        isSelected = selectedScore == score,
                        onClick = { selectedScore = score },
                        par = par
                    )
                    else -> ScoreButton(
                        score = score,
                        isSelected = selectedScore == score,
                        onClick = { selectedScore = score },
                        par = par
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Third row: 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            scores.drop(6).take(3).forEach { score ->
                ScoreButton(
                    score = score,
                    isSelected = selectedScore == score,
                    onClick = { selectedScore = score },
                    par = par
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Putts section
        Text(
            text = "Putts",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (0..4).forEach { putts ->
                PuttsButton(
                    putts = if (putts == 4) "≥4" else putts.toString(),
                    isSelected = selectedPutts == putts,
                    onClick = { selectedPutts = putts }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Navigation and finish button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left arrow
            IconButton(
                onClick = { 
                    if (currentHoleNumber > 1) {
                        onNavigateToHole(currentHoleNumber - 1)
                    }
                },
                enabled = currentHoleNumber > 1
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous hole",
                    tint = if (currentHoleNumber > 1) Color.Black else Color.Gray
                )
            }
            
            // Finish Hole button
            Button(
                onClick = {
                    selectedScore?.let { score ->
                        onFinishHole(score, selectedPutts)
                    }
                },
                enabled = selectedScore != null,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Finish Hole $currentHoleNumber",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Right arrow
            IconButton(
                onClick = { 
                    if (currentHoleNumber < totalHoles) {
                        onNavigateToHole(currentHoleNumber + 1)
                    }
                },
                enabled = currentHoleNumber < totalHoles
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next hole",
                    tint = if (currentHoleNumber < totalHoles) Color.Black else Color.Gray
                )
            }
        }
    }
}

