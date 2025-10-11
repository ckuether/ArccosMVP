package org.example.arccosmvp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import org.jetbrains.compose.resources.ExperimentalResourceApi
import arccosmvp.composeapp.generated.resources.Res
import kotlinx.coroutines.runBlocking
import androidx.core.graphics.scale
import com.example.core_ui.components.createTargetCircleAsset

/**
 * Android-specific helper for creating map markers using VectorDrawable to Bitmap approach
 */
class AndroidDrawableHelper(private val context: Context) {

    companion object {
        private val MARKER_SIZE = 24.dp
        private val TARGET_MARKER_SIZE = 48.dp
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
     * Creates a BitmapDescriptor for Google Maps target circle markers
     */
    fun createTargetCircleMarker(): BitmapDescriptor? {
        return try {
            createTargetCircleAsset(TARGET_MARKER_SIZE.value)?.let { bitmap ->
                BitmapDescriptorFactory.fromBitmap(bitmap as Bitmap)
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
                resizeBitmap(bitmap, MARKER_SIZE.value.toInt())
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
                resizeBitmap(bitmap, MARKER_SIZE.value.toInt())
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