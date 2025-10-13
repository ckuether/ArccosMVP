package com.example.core_ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset

@Composable
fun PolylineComponent(
    points: List<IntOffset>,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    strokeWidth: Float = 2f,
    pathEffect: PathEffect? = null
) {
    if (points.size < 2) return
    
    Canvas(
        modifier = modifier
    ) {
        val path = Path()
        
        // Move to first point
        path.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
        
        // Draw lines to subsequent points
        for (i in 1 until points.size) {
            path.lineTo(points[i].x.toFloat(), points[i].y.toFloat())
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                pathEffect = pathEffect
            )
        )
    }
}