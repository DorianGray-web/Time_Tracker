package com.example.timetracker.utils

import android.content.Context
import android.net.Uri
import com.example.timetracker.domain.model.WorkEntry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object BackupUtils {
    private val json = Json { prettyPrint = true }
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")

    suspend fun createBackup(context: Context, entries: List<WorkEntry>): File {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        val backupFile = File(context.getExternalFilesDir(null), "time_tracker_backup_$timestamp.json")
        
        try {
            val jsonString = json.encodeToString(entries)
            FileOutputStream(backupFile).use { output ->
                output.write(jsonString.toByteArray())
            }
            Timber.d("Backup created successfully: ${backupFile.absolutePath}")
            return backupFile
        } catch (e: Exception) {
            Timber.e(e, "Failed to create backup")
            throw e
        }
    }

    suspend fun restoreFromBackup(context: Context, uri: Uri): List<WorkEntry> {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonString = reader.readText()
                json.decodeFromString<List<WorkEntry>>(jsonString)
            } ?: throw IllegalStateException("Failed to read backup file")
        } catch (e: Exception) {
            Timber.e(e, "Failed to restore from backup")
            throw e
        }
    }
} 