package com.example.timetracker.data

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "locale_settings")

object LocaleManager {
    private object PreferencesKeys {
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
    }

    fun getLanguage(context: Context): String {
        return runBlocking {
            context.dataStore.data.first()[PreferencesKeys.SELECTED_LANGUAGE] ?: "en"
        }
    }

    fun updateLocale(base: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        return base.createConfigurationContext(config)
    }

    suspend fun setLanguage(context: Context, languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = languageCode
        }
    }
} 