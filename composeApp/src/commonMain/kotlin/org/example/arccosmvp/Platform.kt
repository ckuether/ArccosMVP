package org.example.arccosmvp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform