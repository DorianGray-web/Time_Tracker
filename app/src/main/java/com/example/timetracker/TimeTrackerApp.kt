package com.example.timetracker

import android.app.Application
import android.content.Context
import com.example.timetracker.utils.LocaleUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TimeTrackerApp : Application() {
    @Inject
    lateinit var localeUtils: LocaleUtils

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { localeUtils.updateLocale(localeUtils.getLanguage()) })
    }
} 