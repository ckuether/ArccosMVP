package org.example.arccosmvp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import org.example.arccosmvp.R
import androidx.core.graphics.createBitmap

/**
 * Android-specific helper for creating map markers using VectorDrawable to Bitmap approach
 */
object AndroidDrawableHelper {
    
    /**
     * Creates a BitmapDescriptor for Google Maps markers using the VectorDrawable approach
     */
    @Composable
    fun createGolfBallMarker(): BitmapDescriptor? {
        val context = LocalContext.current
        
        return try {
            // Create a golf ball bitmap using the VectorDrawable pattern
            getBitmapFromGolfBallDrawable(context)?.let { bitmap ->
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
    @Composable
    fun createGolfFlagMarker(): BitmapDescriptor? {
        val context = LocalContext.current
        
        return try {
            // Create a golf flag bitmap using the VectorDrawable pattern
            getBitmapFromGolfFlagDrawable(context)?.let { bitmap ->
                BitmapDescriptorFactory.fromBitmap(bitmap)
            }
        } catch (e: Exception) {
            // Fallback to default marker
            null
        }
    }
    
    /**
     * Creates a golf ball bitmap using your PNG asset and VectorDrawable approach
     */
    private fun getBitmapFromGolfBallDrawable(context: Context): Bitmap? {
        // Get the actual golf ball drawable from your PNG asset
        val drawable = ContextCompat.getDrawable(context, R.drawable.golf_ball) ?: return null

        // Calculate size for map marker (12dp - scaled down 4x from 48dp)
        val density = context.resources.displayMetrics.density
        val sizePx = (18 * density).toInt()

        // Create bitmap following your suggested VectorDrawable pattern
        val bitmap = createBitmap(sizePx, sizePx)
        val canvas = Canvas(bitmap)
        
        // Set bounds and draw - exactly your pattern
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
    
    /**
     * Creates a golf flag bitmap using your PNG asset and VectorDrawable approach
     */
    private fun getBitmapFromGolfFlagDrawable(context: Context): Bitmap? {
        // Get the actual golf flag drawable from your PNG asset
        val drawable = ContextCompat.getDrawable(context, R.drawable.golf_flag) ?: return null

        // Calculate size for map marker (18dp - same as golf ball)
        val density = context.resources.displayMetrics.density
        val sizePx = (18 * density).toInt()

        // Create bitmap following your suggested VectorDrawable pattern
        val bitmap = createBitmap(sizePx, sizePx)
        val canvas = Canvas(bitmap)
        
        // Set bounds and draw - exactly your pattern
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}