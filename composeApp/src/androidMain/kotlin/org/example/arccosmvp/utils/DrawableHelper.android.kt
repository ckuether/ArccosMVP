package org.example.arccosmvp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.core.graphics.createBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import arccosmvp.composeapp.generated.resources.Res
import kotlinx.coroutines.runBlocking
import androidx.core.graphics.scale

/**
 * Android-specific helper for creating map markers using VectorDrawable to Bitmap approach
 */
class AndroidDrawableHelper(private val context: Context) {

    companion object {
        private const val MARKER_SIZE = 24
    }
    
    /**
     * Creates a BitmapDescriptor for Google Maps markers using the VectorDrawable approach
     */
    fun createGolfBallMarker(): BitmapDescriptor? {
        return try {
            // Create a golf ball bitmap using the VectorDrawable pattern
            getBitmapFromGolfBallDrawable()?.let { bitmap ->
                BitmapDescriptorFactory.fromBitmap(bitmap)
            }
        } catch (e: Exception) {
            // Fallback to default marker
            null
        }
    }
    
    /**
     * Creates a BitmapDescriptor for Google Maps golf flag markers
     */
    fun createGolfFlagMarker(): BitmapDescriptor? {
        return try {
            // Create a golf flag bitmap using the VectorDrawable pattern
            getBitmapFromGolfFlagDrawable()?.let { bitmap ->
                BitmapDescriptorFactory.fromBitmap(bitmap)
            }
        } catch (e: Exception) {
            // Fallback to default marker
            null
        }
    }
    
    /**
     * Creates a golf ball bitmap using Compose resources
     */
    @OptIn(ExperimentalResourceApi::class)
    private fun getBitmapFromGolfBallDrawable(): Bitmap? {
        return try {
            runBlocking {
                val imageData = Res.readBytes("drawable/golf_ball.png")
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                resizeBitmap(bitmap, MARKER_SIZE)
            }
        } catch (e: Exception) {
            println("Error loading golf ball from Compose resources: ${e.message}")
            null
        }
    }
    
    /**
     * Creates a golf flag bitmap using Compose resources
     */
    @OptIn(ExperimentalResourceApi::class)
    private fun getBitmapFromGolfFlagDrawable(): Bitmap? {
        return try {
            runBlocking {
                val imageData = Res.readBytes("drawable/golf_flag.png")
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                resizeBitmap(bitmap, MARKER_SIZE)
            }
        } catch (e: Exception) {
            println("Error loading golf flag from Compose resources: ${e.message}")
            null
        }
    }
    
    /**
     * Resize bitmap to specified dp size
     */
    @SuppressLint("UseKtx")
    private fun resizeBitmap(bitmap: Bitmap, sizeDp: Int): Bitmap {
        val density = context.resources.displayMetrics.density
        val sizePx = (sizeDp * density).toInt()
        
        return bitmap.scale(sizePx, sizePx)
    }
}