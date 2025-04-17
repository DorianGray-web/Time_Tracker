package com.example.timetracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.timetracker.domain.model.FilterType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "filter_preferences")

@Singleton
class FilterPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val filterKey = stringPreferencesKey("work_entry_filter")

    val filterType: Flow<FilterType> = context.dataStore.data
        .map { preferences ->
            preferences[filterKey]?.let { FilterType.valueOf(it) } ?: FilterType.ALL
        }

    suspend fun updateFilterType(filterType: FilterType) {
        context.dataStore.edit { preferences ->
            preferences[filterKey] = filterType.name
        }
    }
} 