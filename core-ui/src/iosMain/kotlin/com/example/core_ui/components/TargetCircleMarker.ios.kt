package com.example.core_ui.components

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGContextSetStrokeColorWithColor
import platform.CoreGraphics.CGContextSetFillColorWithColor
import platform.CoreGraphics.CGContextStrokeEllipseInRect
import platform.CoreGraphics.CGContextFillEllipseInRect
import platform.CoreGraphics.CGContextSetLineWidth
import platform.UIKit.UIColor
import platform.CoreGraphics.CGSizeMake

@OptIn(ExperimentalForeignApi::class)
actual fun createTargetCircleAsset(sizeDp: Float): Any? {
    return try {
        val scaledSize = sizeDp.toDouble()
        val size = CGSizeMake(scaledSize, scaledSize)
        
        UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
        val context = UIGraphicsGetCurrentContext() ?: return null
        
        // Match the DrawScope logic exactly - use same scaling as Android
        val strokeWidth = 1.0
        val center = scaledSize / 2
        val radius = center - strokeWidth / 2
        
        // Set white color for stroke
        CGContextSetStrokeColorWithColor(context, UIColor.whiteColor.CGColor)
        CGContextSetLineWidth(context, strokeWidth)
        
        // Draw circle outline
        val circleRect = CGRectMake(
            center - radius,
            center - radius,
            radius * 2,
            radius * 2
        )
        CGContextStrokeEllipseInRect(context, circleRect)
        
        // Draw center dot (scaled to match Android)
        val dotRadius = radius * 0.1
        val dotRect = CGRectMake(
            center - dotRadius,
            center - dotRadius,
            dotRadius * 2,
            dotRadius * 2
        )
        CGContextSetFillColorWithColor(context, UIColor.whiteColor.CGColor)
        CGContextFillEllipseInRect(context, dotRect)
        
        val image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        image
    } catch (e: Exception) {
        null
    }
}