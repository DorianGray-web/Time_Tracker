package com.example.timetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.timetracker.datastore.PreferencesManager
import com.example.timetracker.viewmodel.SettingsViewModel
import com.example.timetracker.viewmodel.ThemeMode
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

private val LightColors = lightColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    /* остальное по необходимости */
)

private val DarkColors = darkColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    /* остальное по необходимости */
)

@Composable
fun TimeTrackerTheme(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val themeMode by settingsViewModel.themeMode.collectAsState()

    val colors = when (themeMode) {
        ThemeMode.LIGHT -> LightColors
        ThemeMode.DARK  -> DarkColors
        ThemeMode.SYSTEM ->
            if (isSystemInDarkTheme()) DarkColors else LightColors
    }

    val context = LocalContext.current
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        dynamicColor && isSystemInDarkTheme() -> dynamicDarkColorScheme(context)
        dynamicColor && !isSystemInDarkTheme() -> dynamicLightColorScheme(context)
        isSystemInDarkTheme() -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isSystemInDarkTheme()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 