package com.example.timetracker.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.timetracker.data.dao.TimeEntryDao
import com.example.timetracker.data.model.TimeEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class TimeEntryDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: TimeEntryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = database.timeEntryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_get_entry_by_id() = runTest {
        val entry = TimeEntry(
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now().plusHours(1),
            description = "Test Entry",
            isRunning = false
        )
        val id = dao.insertEntry(entry)
        val loaded = dao.getEntryById(id)

        Assert.assertNotNull(loaded)
        Assert.assertEquals("Test Entry", loaded?.description)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_observe_all_entries() = runTest {
        val entry1 = TimeEntry(startTime = LocalDateTime.now(), description = "Entry 1")
        val entry2 = TimeEntry(startTime = LocalDateTime.now(), description = "Entry 2")
        dao.insertEntry(entry1)
        dao.insertEntry(entry2)

        val entries = dao.getAllEntries().first()
        Assert.assertEquals(2, entries.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun update_entry() = runTest {
        val entry = TimeEntry(startTime = LocalDateTime.now(), description = "Initial")
        val id = dao.insertEntry(entry)

        val updated = entry.copy(id = id, description = "Updated")
        dao.updateEntry(updated)

        val loaded = dao.getEntryById(id)
        Assert.assertEquals("Updated", loaded?.description)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun delete_entry() = runTest {
        val entry = TimeEntry(startTime = LocalDateTime.now(), description = "To be deleted")
        val id = dao.insertEntry(entry)

        val inserted = dao.getEntryById(id)
        Assert.assertNotNull(inserted)

        inserted?.let { dao.deleteEntry(it) }
        val deleted = dao.getEntryById(id)

        Assert.assertNull(deleted)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_running_entry() = runTest {
        val runningEntry = TimeEntry(
            startTime = LocalDateTime.now(),
            description = "Running Entry",
            isRunning = true
        )
        dao.insertEntry(runningEntry)

        val loaded = dao.getRunningEntry()
        Assert.assertNotNull(loaded)
        Assert.assertTrue(loaded?.isRunning == true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_entries_between_dates() = runTest {
        val now = LocalDateTime.now()
        val startDate = now.minusDays(1)
        val endDate = now.plusDays(1)

        val entry1 = TimeEntry(
            startTime = startDate.plusHours(1),
            description = "Entry 1"
        )
        val entry2 = TimeEntry(
            startTime = endDate.minusHours(1),
            description = "Entry 2"
        )
        val entry3 = TimeEntry(
            startTime = endDate.plusDays(1),
            description = "Entry 3"
        )

        dao.insertEntry(entry1)
        dao.insertEntry(entry2)
        dao.insertEntry(entry3)

        val entries = dao.getEntriesBetweenDates(
            startDate = startDate.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
            endDate = endDate.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        ).first()

        Assert.assertEquals(2, entries.size)
        Assert.assertTrue(entries.all { it.description in listOf("Entry 1", "Entry 2") })
    }
} 