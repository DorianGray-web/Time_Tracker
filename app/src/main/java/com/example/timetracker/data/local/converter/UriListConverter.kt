package com.example.timetracker.data.local.converter

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UriListConverter {
    @TypeConverter
    fun fromUriList(value: List<Uri>?): String? {
        if (value == null) return null
        return value.joinToString(",") { it.toString() }
    }

    @TypeConverter
    fun toUriList(value: String?): List<Uri>? {
        if (value == null) return null
        return value.split(",").filter { it.isNotEmpty() }.map { Uri.parse(it) }
    }
} 