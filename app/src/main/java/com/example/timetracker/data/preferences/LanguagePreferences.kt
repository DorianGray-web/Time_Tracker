package com.example.timetracker.data.preferences

import com.example.timetracker.data.PreferencesManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguagePreferences @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    val language: Flow<String> = preferencesManager.language

    suspend fun updateLanguage(language: String) {
        preferencesManager.setLanguage(language)
    }
} 