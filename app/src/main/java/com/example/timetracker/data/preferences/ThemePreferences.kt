package com.example.timetracker.data.preferences

import com.example.timetracker.data.PreferencesManager
import com.example.timetracker.viewmodel.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferences @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    val themeMode: Flow<ThemeMode> = preferencesManager.darkTheme.map { isDark ->
        if (isDark) ThemeMode.DARK else ThemeMode.LIGHT
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        preferencesManager.setDarkTheme(mode == ThemeMode.DARK)
    }
} 