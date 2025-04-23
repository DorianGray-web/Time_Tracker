package com.example.timetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.timetracker.data.local.Converters
import com.example.timetracker.domain.model.WorkEntry
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "work_entries")
@TypeConverters(Converters::class)
data class WorkEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val date: LocalDate,
    val workTypeId: Long,
    val description: String? = null
)

fun WorkEntryEntity.toWorkEntry(): WorkEntry {
    return WorkEntry(
        id = id,
        startTime = startTime,
        endTime = endTime,
        date = date,
        workTypeId = workTypeId,
        description = description
    )
}

fun WorkEntry.toWorkEntryEntity(): WorkEntryEntity {
    return WorkEntryEntity(
        id = id,
        startTime = startTime,
        endTime = endTime,
        date = date,
        workTypeId = workTypeId,
        description = description
    )
} 