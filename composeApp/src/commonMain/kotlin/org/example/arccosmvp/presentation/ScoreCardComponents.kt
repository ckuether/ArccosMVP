package org.example.arccosmvp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core_ui.resources.LocalDimensionResources

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