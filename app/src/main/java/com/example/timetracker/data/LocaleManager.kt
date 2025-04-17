package com.example.timetracker.data

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "locale_settings")

@Singleton
class LocaleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
    }

    val currentLanguage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] ?: "en"
        }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = languageCode
        }
        updateLocale(languageCode)
    }

    fun getCurrentLocale(): Locale {
        val language = getLanguage()
        return Locale(language)
    }

    fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }

    private fun getLanguage(): String {
        return try {
            context.dataStore.data.value[PreferencesKeys.SELECTED_LANGUAGE] ?: "en"
        } catch (e: Exception) {
            "en"
        }
    }
} 