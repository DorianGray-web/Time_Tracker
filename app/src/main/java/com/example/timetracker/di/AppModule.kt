package com.example.timetracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.timetracker.database.TimeTrackerDatabase
import com.example.timetracker.database.TimeEntryDao
import com.example.timetracker.repository.TimeRepository
import com.example.timetracker.datastore.PreferencesManager
import com.example.timetracker.utils.LocaleUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.room.Room

private val Context.dataStore by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>
    ): PreferencesManager = PreferencesManager(context, dataStore)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TimeTrackerDatabase {
        return Room.databaseBuilder(
            context,
            TimeTrackerDatabase::class.java,
            TimeTrackerDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTimeEntryDao(database: TimeTrackerDatabase): TimeEntryDao {
        return database.timeEntryDao()
    }

    @Provides
    @Singleton
    fun provideTimeRepository(
        @ApplicationContext context: Context,
        timeEntryDao: TimeEntryDao
    ): TimeRepository {
        return TimeRepository(context, timeEntryDao)
    }

    @Provides
    @Singleton
    fun provideLocaleUtils(): LocaleUtils = LocaleUtils()
} 