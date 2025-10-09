package org.example.arccosmvp.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import platform.UIKit.UIImage
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.CoreGraphics.CGSizeMake
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import org.jetbrains.compose.resources.ExperimentalResourceApi
import arccosmvp.composeapp.generated.resources.Res
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import platform.CoreGraphics.*
import platform.UIKit.UIGraphicsGetCurrentContext

/**
 * iOS-specific helper for creating map markers from Compose resources
 * Loads PNG assets from commonMain resources for use as Google Maps markers
 */
class IOSDrawableHelper {
    
    companion object {
        private const val MARKER_SIZE = 24.0
    }
    
    /**
     * Creates an iOS-specific UIImage marker for golf balls using the PNG asset
     */
    @OptIn(ExperimentalResourceApi::class, ExperimentalForeignApi::class)
    fun createGolfBallMarker(): UIImage? {
        return try {
            runBlocking {
                val imageData = Res.readBytes("drawable/golf_ball.png")
                
                memScoped {
                    val bytes = allocArrayOf(imageData)
                    val nsData = NSData.dataWithBytes(bytes, imageData.size.toULong())
                    val originalImage = UIImage.imageWithData(nsData)
                    
                    // Resize to appropriate marker size
                    originalImage?.let { resizeImage(it, MARKER_SIZE, MARKER_SIZE) }
                }
            }
        } catch (e: Exception) {
            println("Error loading golf ball marker: ${e.message}")
            null
        }
    }
    
    /**
     * Creates an iOS-specific UIImage marker for golf flags using the PNG asset
     */
    @OptIn(ExperimentalResourceApi::class, ExperimentalForeignApi::class)
    fun createGolfFlagMarker(): UIImage? {
        return try {
            runBlocking {
                val imageData = Res.readBytes("drawable/golf_flag.png")
                
                memScoped {
                    val bytes = allocArrayOf(imageData)
                    val nsData = NSData.dataWithBytes(bytes, imageData.size.toULong())
                    val originalImage = UIImage.imageWithData(nsData)
                    
                    // Resize to appropriate marker size
                    originalImage?.let { resizeImage(it, MARKER_SIZE, MARKER_SIZE) }
                }
            }
        } catch (e: Exception) {
            println("Error loading golf flag marker: ${e.message}")
            null
        }
    }
    
    /**
     * Creates an iOS-specific UIImage marker for target circles using programmatic drawing
     */
    @OptIn(ExperimentalForeignApi::class)
    fun createTargetCircleMarker(): UIImage? {
        return try {
            val size = CGSizeMake(MARKER_SIZE, MARKER_SIZE)
            UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
            
            // Get the current graphics context
            val context = UIGraphicsGetCurrentContext()
            context?.let { ctx ->
                // Draw target circle programmatically
                drawTargetCircleInContext(ctx, MARKER_SIZE)
            }
            
            val targetImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            
            targetImage
        } catch (e: Exception) {
            println("Error creating target circle marker: ${e.message}")
            null
        }
    }
    
    /**
     * Draws the target circle design matching the TargetCircleMarker component
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun drawTargetCircleInContext(context: CGContextRef, size: Double) {
        val center = size / 2.0
        val radius = size / 2.0 - 2.0
        
        // Set line cap to round to match TargetCircleMarker
        CGContextSetLineCap(context, CGLineCap.kCGLineCapRound)
        
        // Draw outer circle (white stroke) - matching TargetCircleMarker style
        CGContextSetRGBStrokeColor(context, 1.0, 1.0, 1.0, 1.0) // White
        CGContextSetLineWidth(context, 2.0) // Scaled down from 12f
        CGContextAddArc(context, center, center, radius, 0.0, 2.0 * kotlin.math.PI, 0)
        CGContextStrokePath(context)
        
        // Draw center dot - matching TargetCircleMarker style
        CGContextSetRGBFillColor(context, 1.0, 1.0, 1.0, 1.0) // White
        CGContextAddArc(context, center, center, 3.0, 0.0, 2.0 * kotlin.math.PI, 0) // 6dp scaled down to 3dp for 24dp marker
        CGContextFillPath(context)
    }

    /**
     * Resizes a UIImage to the specified width and height
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun resizeImage(image: UIImage, width: Double, height: Double): UIImage? {
        val size = CGSizeMake(width, height)
        UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
        
        val rect = CGRectMake(0.0, 0.0, width, height)
        image.drawInRect(rect)
        
        val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return resizedImage
    }
}