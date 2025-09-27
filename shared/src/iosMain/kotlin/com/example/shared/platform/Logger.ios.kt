package com.example.shared.platform

import platform.Foundation.NSLog

class IOSLogger : Logger {
    override fun verbose(tag: String, message: String) {
        NSLog("[$tag] VERBOSE: $message")
    }
    
    override fun debug(tag: String, message: String) {
        NSLog("[$tag] DEBUG: $message")
    }
    
    override fun info(tag: String, message: String) {
        NSLog("[$tag] INFO: $message")
    }
    
    override fun warn(tag: String, message: String) {
        NSLog("[$tag] WARN: $message")
    }
    
    override fun error(tag: String, message: String) {
        NSLog("[$tag] ERROR: $message")
    }
    
    override fun error(tag: String, message: String, throwable: Throwable) {
        NSLog("[$tag] ERROR: $message - ${throwable.message}")
    }
}

actual fun createLogger(): Logger = IOSLogger()