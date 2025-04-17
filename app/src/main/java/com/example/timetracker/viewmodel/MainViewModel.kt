package com.example.timetracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetracker.data.repository.InMemoryWorkEntryRepository
import com.example.timetracker.domain.model.*
import com.example.timetracker.domain.repository.WorkEntryRepository
import com.example.timetracker.domain.usecase.CalculateOvertimeUseCase
import com.example.timetracker.domain.usecase.CalculateSummaryUseCase
import com.example.timetracker.util.ExportUtils
import com.example.timetracker.data.preferences.FilterPreferences
import com.example.timetracker.utils.UiMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WorkEntryRepository,
    private val filterPreferences: FilterPreferences,
    @ApplicationContext private val context: Context,
    private val uiMessageManager: UiMessageManager,
    private val hourlyRate: Double = 15.0,
    private val weeklyHours: Double = 40.0,
    private val overtimeMultiplier: Double = 1.5
) : BaseViewModel(uiMessageManager) {

    private val _entries = MutableStateFlow<List<WorkEntry>>(emptyList())
    val entries: StateFlow<List<WorkEntry>> = _entries.asStateFlow()

    private val _summary = MutableStateFlow<WorkSummary?>(null)
    val summary: StateFlow<WorkSummary?> = _summary.asStateFlow()

    private val _exportFormat = MutableStateFlow(ExportFormat.PDF)
    val exportFormat: StateFlow<ExportFormat> = _exportFormat.asStateFlow()

    private val _filterType = MutableStateFlow<FilterType>(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType.asStateFlow()

    private val _sortOptions = MutableStateFlow(SortOptions())
    val sortOptions: StateFlow<SortOptions> = _sortOptions.asStateFlow()

    private val calculateOvertimeUseCase = CalculateOvertimeUseCase(weeklyHours, overtimeMultiplier)
    private val calculateSummaryUseCase = CalculateSummaryUseCase(hourlyRate, weeklyHours, overtimeMultiplier)

    init {
        loadFilterType()
        loadEntries()
    }

    private fun loadFilterType() {
        viewModelScope.launch {
            filterPreferences.filterType.collect { type ->
                _filterType.value = type
            }
        }
    }

    private fun loadEntries() {
        safeCall(
            block = {
                repository.getAllEntries()
                    .combine(_filterType) { entries, filter ->
                        when (filter) {
                            FilterType.ALL -> entries
                            else -> repository.getFilteredEntries(filter)
                        }
                    }
                    .collect { entries ->
                        _entries.value = entries
                        updateSummary(entries)
                    }
            },
            onError = { error ->
                Timber.e(error, "Error loading entries")
                showError("Failed to load entries: ${error.message}")
            }
        )
    }

    private fun filterEntries(entries: List<WorkEntry>, filter: FilterOptions): List<WorkEntry> {
        return entries.filter { entry ->
            val matchesDateRange = when {
                filter.startDate != null && filter.endDate != null -> 
                    entry.startTime.toLocalDate() >= filter.startDate && 
                    entry.endTime.toLocalDate() <= filter.endDate
                filter.startDate != null -> 
                    entry.startTime.toLocalDate() >= filter.startDate
                filter.endDate != null -> 
                    entry.endTime.toLocalDate() <= filter.endDate
                else -> true
            }

            val matchesWeek = filter.weekNumber?.let { week ->
                val weekFields = WeekFields.of(Locale.getDefault())
                entry.startTime.get(weekFields.weekOfWeekBasedYear()) == week
            } ?: true

            val matchesProject = filter.projectName?.let { project ->
                entry.description.contains(project, ignoreCase = true)
            } ?: true

            val matchesClient = filter.clientName?.let { client ->
                entry.description.contains(client, ignoreCase = true)
            } ?: true

            val matchesOvertime = when (filter.showOvertime) {
                true -> entry.getDurationHours() > weeklyHours
                false -> true
            }

            val matchesPhotos = when (filter.showOnlyWithPhotos) {
                true -> entry.photos.isNotEmpty()
                false -> true
            }

            matchesDateRange && matchesWeek && matchesProject && 
            matchesClient && matchesOvertime && matchesPhotos
        }
    }

    private fun sortEntries(entries: List<WorkEntry>, sort: SortOptions): List<WorkEntry> {
        return when (sort.field) {
            SortField.START_TIME -> entries.sortedBy { it.startTime }
            SortField.DURATION -> entries.sortedBy { it.getDurationHours() }
            SortField.COST -> entries.sortedBy { it.materialsCost }
            SortField.DATE -> entries.sortedBy { it.startTime.toLocalDate() }
        }.let { sorted ->
            when (sort.order) {
                SortOrder.ASCENDING -> sorted
                SortOrder.DESCENDING -> sorted.reversed()
            }
        }
    }

    fun updateFilterType(type: FilterType) {
        viewModelScope.launch {
            _filterType.value = type
            filterPreferences.updateFilterType(type)
        }
    }

    fun updateSortOptions(options: SortOptions) {
        _sortOptions.value = options
    }

    fun addEntry(entry: WorkEntry) {
        viewModelScope.launch {
            repository.addEntry(entry)
        }
    }

    fun updateEntry(entry: WorkEntry) {
        safeCall(
            block = {
                repository.updateEntry(entry)
                _entries.value = _entries.value.map { 
                    if (it.id == entry.id) entry else it 
                }
                updateSummary(_entries.value)
                showSuccess("Entry updated successfully")
            },
            onError = { error ->
                Timber.e(error, "Error updating entry")
                showError("Failed to update entry: ${error.message}")
            }
        )
    }

    fun deleteEntry(entryId: Long) {
        safeCall(
            block = {
                repository.deleteEntry(entryId)
                _entries.value = _entries.value.filter { it.id != entryId }
                updateSummary(_entries.value)
                showSuccess("Entry deleted successfully")
            },
            onError = { error ->
                Timber.e(error, "Error deleting entry")
                showError("Failed to delete entry: ${error.message}")
            }
        )
    }

    fun getEntry(entryId: Long): WorkEntry? {
        return _entries.value.find { it.id == entryId }
    }

    fun getEntriesForWeek(weekStart: LocalDate) {
        viewModelScope.launch {
            repository.getEntriesForWeek(weekStart)
                .distinctUntilChanged()
                .collect { entries ->
                    _entries.value = entries
                    updateSummary(entries)
                }
        }
    }

    private fun updateSummary(entries: List<WorkEntry>) {
        _summary.value = calculateSummaryUseCase.execute(entries)
    }

    fun setExportFormat(format: ExportFormat) {
        _exportFormat.value = format
    }

    fun exportEntries() {
        viewModelScope.launch {
            val entries = _entries.value
            val summary = _summary.value ?: return@launch
            
            val file = when (_exportFormat.value) {
                ExportFormat.CSV -> ExportUtils.generateCsv(entries, context)
                ExportFormat.PDF -> ExportUtils.generatePdf(entries, summary, context)
            }
            
            ExportUtils.shareFile(
                file,
                context,
                when (_exportFormat.value) {
                    ExportFormat.CSV -> "text/csv"
                    ExportFormat.PDF -> "application/pdf"
                }
            )
        }
    }
}

enum class ExportFormat {
    CSV, PDF
} 