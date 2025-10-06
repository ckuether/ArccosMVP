package com.example.core_ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun TargetCircleMarker(
    modifier: Modifier = Modifier,
    size: Float = 120f,
    primaryColor: Color = Color.Black,
    secondaryColor: Color = Color.White
) {
    Canvas(
        modifier = modifier.size(size.dp)
    ) {
        drawTargetCircle(
            size = this.size.width,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor
        )
    }
}

fun DrawScope.drawTargetCircle(
    size: Float,
    primaryColor: Color = Color.Black,
    secondaryColor: Color = Color.White
) {
    val center = Offset(size / 2f, size / 2f)
    val radius = size / 2f - 20f
    
    // Draw outer circle (white stroke)
    drawCircle(
        color = secondaryColor,
        radius = radius,
        center = center,
        style = Stroke(width = 12f, cap = StrokeCap.Round)
    )
    
    // Draw inner circle (black stroke) 
    drawCircle(
        color = primaryColor,
        radius = radius,
        center = center,
        style = Stroke(width = 6f, cap = StrokeCap.Round)
    )
    
    // Draw crosshairs
    val crosshairLength = radius / 2f
    
    // Horizontal line
    drawLine(
        color = primaryColor,
        start = Offset(center.x - crosshairLength, center.y),
        end = Offset(center.x + crosshairLength, center.y),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )
    
    // Vertical line
    drawLine(
        color = primaryColor,
        start = Offset(center.x, center.y - crosshairLength),
        end = Offset(center.x, center.y + crosshairLength),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )
    
    // Draw center dot
    drawCircle(
        color = primaryColor,
        radius = 8f,
        center = center
    )
}