package com.example.timetracker.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.timetracker.domain.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val WEEKLY_HOURS = intPreferencesKey("weekly_hours")
        private val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val weeklyHoursFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[WEEKLY_HOURS] ?: 40 }

    val selectedLanguageFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[SELECTED_LANGUAGE] ?: "en" }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            when (preferences[THEME_MODE]) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                "system" -> ThemeMode.SYSTEM
                else -> ThemeMode.SYSTEM
            }
        }

    suspend fun setWeeklyHours(hours: Int) {
        context.dataStore.edit { preferences ->
            preferences[WEEKLY_HOURS] = hours
        }
    }

    suspend fun setSelectedLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_LANGUAGE] = language
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = when (mode) {
                ThemeMode.LIGHT -> "light"
                ThemeMode.DARK -> "dark"
                ThemeMode.SYSTEM -> "system"
            }
        }
    }
} 