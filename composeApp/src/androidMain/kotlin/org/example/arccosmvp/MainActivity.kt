package org.example.arccosmvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.shared.platform.ActivityContextProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Set this activity as the current activity for permission requests
        ActivityContextProvider.setCurrentActivity(this)

        setContent {
            App()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clear the activity reference when activity is destroyed
        ActivityContextProvider.clearCurrentActivity()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}