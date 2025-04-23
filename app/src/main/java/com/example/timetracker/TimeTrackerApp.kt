package com.example.timetracker

import android.app.Application
import android.content.Context
import com.example.timetracker.data.LocaleManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TimeTrackerApp : Application() {
    override fun attachBaseContext(base: Context) {
        val language = LocaleManager.getLanguage(base)
        val localizedContext = LocaleManager.updateLocale(base, language)
        super.attachBaseContext(localizedContext)
    }
} 