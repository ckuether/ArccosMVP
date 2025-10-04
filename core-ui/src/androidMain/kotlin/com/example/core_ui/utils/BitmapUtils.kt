package com.example.core_ui.utils

import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas as ComposeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.createBitmap
import com.example.core_ui.components.drawTargetCircle
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object BitmapUtils {
    
    fun createTargetCircleBitmap(
        size: Int = 120,
        primaryColor: Color = Color.Black,
        secondaryColor: Color = Color.White
    ): BitmapDescriptor {
        val bitmap = createBitmap(size, size)
        val canvas = AndroidCanvas(bitmap)
        
        // Create a custom DrawScope implementation for Android Canvas
        val drawScope = AndroidCanvasDrawScope(canvas, size.toFloat())
        
        // Use our common drawing logic
        with(drawScope) {
            drawTargetCircle(
                size = size.toFloat(),
                primaryColor = primaryColor,
                secondaryColor = secondaryColor
            )
        }
        
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

// Simple DrawScope implementation for Android Canvas
private class AndroidCanvasDrawScope(
    private val canvas: AndroidCanvas,
    private val canvasSize: Float
) : DrawScope {

    override val density: Float
        get() = 1f
    override val fontScale: Float
        get() = 1f
    override val size: Size
        get() = Size(canvasSize, canvasSize)
        
    override val center: Offset
        get() = Offset(canvasSize / 2f, canvasSize / 2f)
        
    override val layoutDirection: LayoutDirection
        get() = LayoutDirection.Ltr
        
    override fun drawLine(
        color: Color,
        start: Offset,
        end: Offset,
        strokeWidth: Float,
        cap: StrokeCap,
        pathEffect: PathEffect?,
        alpha: Float,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) {
        val paint = Paint().apply {
            this.color = color.toArgb()
            this.strokeWidth = strokeWidth
            this.isAntiAlias = true
            this.strokeCap = when (cap) {
                StrokeCap.Round -> Paint.Cap.ROUND
                StrokeCap.Square -> Paint.Cap.SQUARE
                else -> Paint.Cap.BUTT
            }
        }
        this.canvas.drawLine(start.x, start.y, end.x, end.y, paint)
    }
    
    override fun drawCircle(
        color: Color,
        radius: Float,
        center: Offset,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) {
        val paint = Paint().apply {
            this.color = color.toArgb()
            this.isAntiAlias = true
            when (style) {
                is Stroke -> {
                    this.style = Paint.Style.STROKE
                    this.strokeWidth = style.width
                }
                else -> {
                    this.style = Paint.Style.FILL
                }
            }
        }
        this.canvas.drawCircle(center.x, center.y, radius, paint)
    }
    
    // Implement other required methods with no-op or basic implementations
    override fun drawArc(
        color: Color,
        startAngle: Float,
        sweepAngle: Float,
        useCenter: Boolean,
        topLeft: Offset,
        size: Size,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawImage(
        image: ImageBitmap,
        topLeft: Offset,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    
    override fun drawOval(
        color: Color,
        topLeft: Offset,
        size: Size,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawPath(
        path: Path,
        color: Color,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawPoints(
        points: List<Offset>,
        pointMode: PointMode,
        color: Color,
        strokeWidth: Float,
        cap: StrokeCap,
        pathEffect: PathEffect?,
        alpha: Float,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawRect(
        color: Color,
        topLeft: Offset,
        size: Size,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawRoundRect(
        color: Color,
        topLeft: Offset,
        size: Size,
        cornerRadius: CornerRadius,
        style: DrawStyle,
        alpha: Float,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    // Additional required methods with Brush parameter
    override fun drawLine(
        brush: Brush,
        start: Offset,
        end: Offset,
        strokeWidth: Float,
        cap: StrokeCap,
        pathEffect: PathEffect?,
        alpha: Float,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawCircle(
        brush: Brush,
        radius: Float,
        center: Offset,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawArc(
        brush: Brush,
        startAngle: Float,
        sweepAngle: Float,
        useCenter: Boolean,
        topLeft: Offset,
        size: Size,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawOval(
        brush: Brush,
        topLeft: Offset,
        size: Size,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawPath(
        path: Path,
        brush: Brush,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawPoints(
        points: List<Offset>,
        pointMode: PointMode,
        brush: Brush,
        strokeWidth: Float,
        cap: StrokeCap,
        pathEffect: PathEffect?,
        alpha: Float,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawRect(
        brush: Brush,
        topLeft: Offset,
        size: Size,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawRoundRect(
        brush: Brush,
        topLeft: Offset,
        size: Size,
        cornerRadius: CornerRadius,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override fun drawImage(
        image: ImageBitmap,
        srcOffset: IntOffset,
        srcSize: IntSize,
        dstOffset: IntOffset,
        dstSize: IntSize,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) = Unit
    
    override val drawContext: DrawContext
        get() = object : DrawContext {
            override var canvas: ComposeCanvas
                get() = TODO("Not implemented")
                set(value) = TODO("Not implemented")
            override var size: Size
                get() = this@AndroidCanvasDrawScope.size
                set(value) = TODO("Not implemented")
            override var transform: DrawTransform
                get() = TODO("Not implemented")
                set(value) = TODO("Not implemented")
        }
}