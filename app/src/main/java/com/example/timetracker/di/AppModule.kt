package com.example.timetracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.timetracker.data.local.TimeTrackerDatabase
import com.example.timetracker.data.local.dao.WorkEntryDao
import com.example.timetracker.data.PreferencesManager
import com.example.timetracker.data.preferences.ThemePreferences
import com.example.timetracker.data.preferences.LanguagePreferences
import com.example.timetracker.data.datastore.SettingsDataStore
import com.example.timetracker.data.repository.WorkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.room.Room

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.filesDir.resolve("datastore/settings.preferences_pb") }
        )
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager = PreferencesManager(context)

    @Provides
    @Singleton
    fun provideThemePreferences(preferencesManager: PreferencesManager): ThemePreferences {
        return ThemePreferences(preferencesManager)
    }

    @Provides
    @Singleton
    fun provideLanguagePreferences(preferencesManager: PreferencesManager): LanguagePreferences {
        return LanguagePreferences(preferencesManager)
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore {
        return SettingsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TimeTrackerDatabase {
        return TimeTrackerDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideWorkEntryDao(database: TimeTrackerDatabase): WorkEntryDao {
        return database.workEntryDao()
    }

    @Provides
    @Singleton
    fun provideWorkRepository(workEntryDao: WorkEntryDao): WorkRepository {
        return WorkRepository(workEntryDao)
    }
} 