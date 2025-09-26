package com.example.shared.platform

import androidx.activity.ComponentActivity
import java.lang.ref.WeakReference

object ActivityContextProvider {
    private var currentActivity: WeakReference<ComponentActivity>? = null
    
    fun setCurrentActivity(activity: ComponentActivity) {
        currentActivity = WeakReference(activity)
    }
    
    fun getCurrentActivity(): ComponentActivity? {
        return currentActivity?.get()
    }
    
    fun clearCurrentActivity() {
        currentActivity?.clear()
        currentActivity = null
    }
}