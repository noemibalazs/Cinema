package com.noemi.cinema

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform