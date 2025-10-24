package com.example.round_of_golf_presentation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.core_ui.components.DraggableBottomSheetWrapper
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.data.model.Hole
import com.example.shared.utils.StringResources
import com.example.core_ui.utils.UiText

@Composable
fun HoleStatsBottomSheet(
    currentHole: Hole?,
    currentHoleNumber: Int,
    totalHoles: Int,
    existingScore: Int? = null,
    onDismiss: () -> Unit,
    onFinishHole: (score: Int, putts: Int) -> Unit,
    prevHoleClicked: () -> Unit,
    nextHoleClicked: () -> Unit
) {
    DraggableBottomSheetWrapper(
        onDismiss = onDismiss,
        fillMaxHeight = 0.85f
    ) {
        HoleStats(
            currentHole = currentHole,
            currentHoleNumber = currentHoleNumber,
            totalHoles = totalHoles,
            existingScore = existingScore,
            onDismiss = onDismiss,
            onFinishHole = onFinishHole,
            prevHoleClicked = prevHoleClicked,
            nextHoleClicked = nextHoleClicked
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoleStats(
    currentHole: Hole?,
    currentHoleNumber: Int,
    totalHoles: Int,
    existingScore: Int? = null,
    onDismiss: () -> Unit,
    onFinishHole: (score: Int, putts: Int) -> Unit,
    prevHoleClicked: () -> Unit,
    nextHoleClicked: () -> Unit
) {
    val dimensions = LocalDimensionResources.current
    var selectedScore by remember(currentHoleNumber) { mutableStateOf(existingScore) }
    var selectedPutts by remember(currentHoleNumber) { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingXXLarge)
    ) {
        // Score section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = UiText.StringResourceId(StringResources.holeScoreTemplate, arrayOf(currentHoleNumber)).asString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = UiText.StringResourceId(StringResources.others).asString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spacingLarge))

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

        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

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

        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

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

        Spacer(modifier = Modifier.height(dimensions.spacingXLarge))

        // Putts section
        Text(
            text = UiText.StringResourceId(StringResources.putts).asString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

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

        Spacer(modifier = Modifier.height(dimensions.spacingXLarge))

        // Navigation and finish button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left arrow
            IconButton(
                onClick = {
                    prevHoleClicked()
                },
                enabled = currentHoleNumber > 1
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = UiText.StringResourceId(StringResources.previousHole).asString(),
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
                    .padding(horizontal = dimensions.paddingLarge)
                    .height(dimensions.buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(dimensions.buttonCornerRadius)
            ) {
                Text(
                    text = if (currentHoleNumber == totalHoles) UiText.StringResourceId(StringResources.finishRound).asString() else UiText.StringResourceId(StringResources.finishHoleTemplate, arrayOf(currentHoleNumber)).asString(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Right arrow
            IconButton(
                onClick = {
                    nextHoleClicked()
                },
                enabled = currentHoleNumber < totalHoles
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = UiText.StringResourceId(StringResources.nextHole).asString(),
                    tint = if (currentHoleNumber < totalHoles) Color.Black else Color.Gray
                )
            }
        }
    }
}
