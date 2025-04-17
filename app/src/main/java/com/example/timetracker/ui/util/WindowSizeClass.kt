package com.example.timetracker.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

enum class WindowSizeClass {
    COMPACT,    // Phone (< 600dp)
    MEDIUM,     // Small tablet/foldable (600dp - 840dp)
    EXPANDED    // Large tablet (> 840dp)
}

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> WindowSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
} 