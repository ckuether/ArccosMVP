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

/**
 * iOS-specific helper for creating map markers from Compose resources
 * Loads PNG assets from commonMain resources for use as Google Maps markers
 */
class IOSDrawableHelper {
    
    companion object {
        // Standard marker size for Google Maps (similar to Android 18dp)
        private const val MARKER_SIZE = 24.0 // Points (equivalent to ~18dp at 2x)
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