package org.example.arccosmvp.platform

import platform.Foundation.NSDate

actual fun getCurrentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}