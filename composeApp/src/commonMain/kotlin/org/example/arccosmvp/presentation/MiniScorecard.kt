package org.example.arccosmvp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.core_ui.resources.LocalDimensionResources

@Composable
fun MiniScorecard(
    onScoreCardClick: () -> Unit = {}
) {
    val dimensions = LocalDimensionResources.current
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.small,
        onClick = onScoreCardClick
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = dimensions.paddingSmall, 
                vertical = dimensions.paddingSmall
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scorecard",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "+1",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}