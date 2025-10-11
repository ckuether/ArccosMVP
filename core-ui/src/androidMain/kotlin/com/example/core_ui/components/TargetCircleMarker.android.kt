package com.example.core_ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import androidx.core.graphics.createBitmap

actual fun createTargetCircleAsset(sizeDp: Float): Any? {
    return try {
        val density = 3f // Rough dp to px conversion for consistency
        val sizePx = (sizeDp * density).toInt()
        val bitmap = createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Match the DrawScope logic exactly
        val strokeWidthPx = 1f * density // 2.dp equivalent
        val center = sizePx / 2f
        val radius = center - strokeWidthPx / 2
        
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
        }
        
        // Draw white circle outline with thicker stroke for visibility
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidthPx
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawCircle(center, center, radius, paint)
        
        // Draw center dot (solid circle) - 8.dp equivalent
        paint.style = Paint.Style.FILL
        val dotRadius = 4f * density
        canvas.drawCircle(center, center, dotRadius, paint)
        
        bitmap
    } catch (e: Exception) {
        null
    }
}