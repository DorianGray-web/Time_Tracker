package com.example.timetracker.viewmodel

import com.example.timetracker.domain.model.WorkEntry
import com.example.timetracker.domain.repository.WorkEntryRepository
import com.example.timetracker.utils.UiMessageManager
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class MainViewModelTest {
    private lateinit var viewModel: MainViewModel
    private lateinit var repository: WorkEntryRepository
    private lateinit var uiMessageManager: UiMessageManager
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        repository = mockk()
        uiMessageManager = mockk(relaxed = true)
        viewModel = MainViewModel(
            repository = repository,
            filterPreferences = mockk(relaxed = true),
            context = mockk(relaxed = true),
            uiMessageManager = uiMessageManager,
            hourlyRate = 15.0,
            weeklyHours = 40.0,
            overtimeMultiplier = 1.5
        )
    }

    @Test
    fun `loadEntries should handle repository error`() = runBlockingTest {
        // Given
        val error = IOException("Network error")
        coEvery { repository.getAllEntries() } throws error

        // When
        viewModel.loadEntries()

        // Then
        coVerify { uiMessageManager.showError(any()) }
    }

    @Test
    fun `updateEntry should handle repository error`() = runBlockingTest {
        // Given
        val entry = WorkEntry(id = 1, startTime = System.currentTimeMillis())
        val error = IOException("Update failed")
        coEvery { repository.updateEntry(entry) } throws error

        // When
        viewModel.updateEntry(entry)

        // Then
        coVerify { uiMessageManager.showError(any()) }
    }

    @Test
    fun `deleteEntry should handle repository error`() = runBlockingTest {
        // Given
        val entryId = 1L
        val error = IOException("Delete failed")
        coEvery { repository.deleteEntry(entryId) } throws error

        // When
        viewModel.deleteEntry(entryId)

        // Then
        coVerify { uiMessageManager.showError(any()) }
    }

    @Test
    fun `getEntry should return null for non-existent entry`() {
        // Given
        val nonExistentId = 999L
        coEvery { repository.getAllEntries() } returns flowOf(emptyList())

        // When
        val result = viewModel.getEntry(nonExistentId)

        // Then
        assertEquals(null, result)
    }
} 