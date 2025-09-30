package org.example.arccosmvp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import org.example.arccosmvp.R
import androidx.core.graphics.createBitmap

/**
 * Android-specific helper for creating map markers using VectorDrawable to Bitmap approach
 */
class AndroidDrawableHelper(private val context: Context) {
    
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
     * Creates a golf ball bitmap using your PNG asset and VectorDrawable approach
     */
    private fun getBitmapFromGolfBallDrawable(): Bitmap? {
        return createBitmapFromDrawable(R.drawable.golf_ball)
    }
    
    /**
     * Creates a golf flag bitmap using your PNG asset and VectorDrawable approach
     */
    private fun getBitmapFromGolfFlagDrawable(): Bitmap? {
        return createBitmapFromDrawable(R.drawable.golf_flag)
    }
    
    /**
     * Generic function to create bitmap from drawable resource using VectorDrawable approach
     */
    private fun createBitmapFromDrawable(drawableResId: Int, sizeDp: Int = 18): Bitmap? {
        // Get the drawable from resource
        val drawable = ContextCompat.getDrawable(context, drawableResId) ?: return null

        // Calculate size for map marker
        val density = context.resources.displayMetrics.density
        val sizePx = (sizeDp * density).toInt()

        // Create bitmap following VectorDrawable pattern
        val bitmap = createBitmap(sizePx, sizePx)
        val canvas = Canvas(bitmap)
        
        // Set bounds and draw - exactly your pattern
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}