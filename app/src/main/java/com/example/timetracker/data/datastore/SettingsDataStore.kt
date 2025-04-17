package com.example.timetracker.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val WEEKLY_HOURS = intPreferencesKey("weekly_hours")
        val OVERTIME_MULTIPLIER = doublePreferencesKey("overtime_multiplier")
    }

    val weeklyHoursFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.WEEKLY_HOURS] ?: 16 // Default value
        }

    val overtimeMultiplierFlow: Flow<Double> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.OVERTIME_MULTIPLIER] ?: 1.5 // Default value
        }

    suspend fun setWeeklyHours(hours: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEEKLY_HOURS] = hours
        }
    }

    suspend fun setOvertimeMultiplier(multiplier: Double) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.OVERTIME_MULTIPLIER] = multiplier
        }
    }
} 