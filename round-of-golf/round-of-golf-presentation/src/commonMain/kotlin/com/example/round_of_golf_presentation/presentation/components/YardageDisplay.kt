package com.example.round_of_golf_presentation.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.core_ui.resources.LocalDimensionResources

object YardageDisplayDefaults {
    @Composable
    fun getSize(): Dp {
        val dimensions = LocalDimensionResources.current
        return dimensions.iconXLarge
    }
}

@Composable
fun YardageDisplay(
    yardage: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black.copy(alpha = 0.8f),
    textColor: Color = Color.White
) {

    val yardageSize = YardageDisplayDefaults.getSize()

    Box(
        modifier = modifier
            .size(yardageSize)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${yardage}y",
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}