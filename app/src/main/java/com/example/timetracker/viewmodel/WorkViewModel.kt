package com.example.timetracker.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetracker.data.datastore.SettingsDataStore
import com.example.timetracker.data.model.WorkEntry
import com.example.timetracker.data.repository.WorkRepository
import com.example.timetracker.model.FilterOptions
import com.example.timetracker.model.SortType
import com.example.timetracker.util.LocaleHelper
import com.example.timetracker.util.PdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
import androidx.core.content.FileProvider
import android.graphics.Paint
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.graphics.BitmapFactory
import android.graphics.Matrix

enum class SortOption(val label: String) {
    DATE_ASC("Date ↑"),
    DATE_DESC("Date ↓"),
    START_TIME_ASC("Start Time ↑"),
    DURATION_DESC("Duration ↓")
}

sealed class UiEvent {
    data class ShowMessage(val message: String) : UiEvent()
}

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val repository: WorkRepository,
    private val localeHelper: LocaleHelper,
    private val settingsDataStore: SettingsDataStore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var repositoryCache: List<WorkEntry> = emptyList()

    private val _allWorkEntries = MutableStateFlow<List<WorkEntry>>(emptyList())
    private val _workEntries = MutableStateFlow<List<WorkEntry>>(emptyList())
    val workEntries: StateFlow<List<WorkEntry>> = _workEntries.asStateFlow()

    private val _vacationDays = MutableStateFlow<List<LocalDate>>(emptyList())
    val vacationDays: StateFlow<List<LocalDate>> = _vacationDays.asStateFlow()

    private val _weekendDays = MutableStateFlow<List<LocalDate>>(emptyList())
    val weekendDays: StateFlow<List<LocalDate>> = _weekendDays.asStateFlow()

    private val _weeklyHours = MutableStateFlow(16)
    val weeklyHours: StateFlow<Int> = _weeklyHours.asStateFlow()

    private val _overtimeMultiplier = MutableStateFlow(1.5)
    val overtimeMultiplier: StateFlow<Double> = _overtimeMultiplier.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(LocaleHelper.getLanguage(context))
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _dateFilter = MutableStateFlow<Pair<LocalDate?, LocalDate?>>(null to null)
    val dateFilter: StateFlow<Pair<LocalDate?, LocalDate?>> = _dateFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.DATE_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _filterOptions = MutableStateFlow(FilterOptions())
    val filterOptions = _filterOptions.asStateFlow()

    init {
        loadWorkEntries()
        loadVacationDays()
        loadWeekendDays()
        observeSettings()
    }

    private fun loadWorkEntries() {
        viewModelScope.launch {
            val entries = repository.getAllEntries()
            _allWorkEntries.value = entries
            applySortingAndFiltering()
        }
    }

    private fun loadVacationDays() {
        viewModelScope.launch {
            _vacationDays.value = repository.getVacationDays()
        }
    }

    private fun loadWeekendDays() {
        viewModelScope.launch {
            _weekendDays.value = repository.getWeekendDays()
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsDataStore.weeklyHoursFlow.collectLatest { hours ->
                _weeklyHours.value = hours
            }
        }
        viewModelScope.launch {
            settingsDataStore.overtimeMultiplierFlow.collectLatest { multiplier ->
                _overtimeMultiplier.value = multiplier
            }
        }
    }

    fun updateWeeklyHours(hours: Int) {
        viewModelScope.launch {
            settingsDataStore.setWeeklyHours(hours)
        }
    }

    fun updateOvertimeMultiplier(multiplier: Double) {
        viewModelScope.launch {
            settingsDataStore.setOvertimeMultiplier(multiplier)
        }
    }

    fun updateLanguage(languageCode: String) {
        _selectedLanguage.value = languageCode
        LocaleHelper.setLocale(context, languageCode)
    }

    fun updateDateFilter(start: LocalDate?, end: LocalDate?) {
        _dateFilter.value = start to end
        applySortingAndFiltering()
    }

    fun updateSortOption(option: SortOption) {
        _sortOption.value = option
        applySortingAndFiltering()
    }

    fun updateFilterOptions(options: FilterOptions) {
        _filterOptions.value = options
        applySortingAndFiltering()
    }

    private fun applySortingAndFiltering() {
        viewModelScope.launch {
            val filteredEntries = _workEntries.value.filter { entry ->
                val filterOptions = _filterOptions.value
                val matchesDateRange = when {
                    filterOptions.startDate != null && filterOptions.endDate != null -> 
                        entry.date.isAfter(filterOptions.startDate) && 
                        entry.date.isBefore(filterOptions.endDate)
                    filterOptions.startDate != null -> 
                        entry.date.isAfter(filterOptions.startDate)
                    filterOptions.endDate != null -> 
                        entry.date.isBefore(filterOptions.endDate)
                    else -> true
                }

                val matchesOvertime = when (filterOptions.showOvertime) {
                    true -> entry.getDuration() > 8
                    false -> true
                }

                val matchesHasPhoto = when (filterOptions.showOnlyWithPhotos) {
                    true -> entry.photoPath != null
                    false -> true
                }

                matchesDateRange && matchesOvertime && matchesHasPhoto
            }

            val sortedEntries = when (_sortOption.value) {
                SortOption.DATE_ASC -> filteredEntries.sortedBy { it.date }
                SortOption.DATE_DESC -> filteredEntries.sortedByDescending { it.date }
                SortOption.START_TIME_ASC -> filteredEntries.sortedBy { it.startTime }
                SortOption.DURATION_DESC -> filteredEntries.sortedByDescending { it.getDuration() }
            }

            _workEntries.value = sortedEntries
        }
    }

    suspend fun addWorkEntry(entry: WorkEntry) {
        try {
            repository.addEntry(entry)
            _uiEvent.emit(UiEvent.ShowMessage("Entry added successfully"))
        } catch (e: Exception) {
            _uiEvent.emit(UiEvent.ShowMessage("Failed to add entry: ${e.message}"))
        }
    }

    suspend fun updateWorkEntry(entry: WorkEntry) {
        try {
            repository.updateEntry(entry)
            _uiEvent.emit(UiEvent.ShowMessage("Entry updated successfully"))
        } catch (e: Exception) {
            _uiEvent.emit(UiEvent.ShowMessage("Failed to update entry: ${e.message}"))
        }
    }

    suspend fun deleteWorkEntry(entry: WorkEntry) {
        try {
            repository.deleteEntry(entry)
            _uiEvent.emit(UiEvent.ShowMessage("Entry deleted successfully"))
        } catch (e: Exception) {
            _uiEvent.emit(UiEvent.ShowMessage("Failed to delete entry: ${e.message}"))
        }
    }

    private fun savePhoto(uri: Uri): String? {
        val fileName = "work_photo_${UUID.randomUUID()}.jpg"
        val file = File(context.getExternalFilesDir(null), fileName)
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun addWorkEntry(
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime,
        commentEn: String,
        commentNl: String,
        photoUri: Uri?
    ) {
        viewModelScope.launch {
            val photoPath = photoUri?.let { uri ->
                val fileName = "work_photo_${UUID.randomUUID()}.jpg"
                val file = File(context.filesDir, fileName)
                try {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    file.absolutePath
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            val workEntry = WorkEntry(
                date = date,
                startTime = startTime,
                endTime = endTime,
                commentEn = commentEn,
                commentNl = commentNl,
                photoPath = photoPath
            )

            repository.insertWorkEntry(workEntry)
            loadWorkEntries()
        }
    }

    fun calculateSalary(hourlyRate: Double): Double {
        val totalHoursWorked = _workEntries.value.sumOf { it.getDuration() }
        val overtimeHours = (totalHoursWorked - _weeklyHours.value).coerceAtLeast(0.0)
        val regularHours = totalHoursWorked - overtimeHours
        return (regularHours * hourlyRate) + (overtimeHours * hourlyRate * _overtimeMultiplier.value)
    }

    private fun cleanupOldReports() {
        try {
            val cacheDir = context.cacheDir
            val twoWeeksAgo = System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000) // 14 days in milliseconds
            var deletedCount = 0

            cacheDir.listFiles()?.forEach { file ->
                if ((file.name.endsWith(".pdf") || file.name.endsWith(".csv")) && 
                    file.lastModified() < twoWeeksAgo) {
                    if (file.delete()) {
                        deletedCount++
                    }
                }
            }

            if (deletedCount > 0) {
                android.util.Log.d("WorkViewModel", "Cleaned up $deletedCount old report files")
            }
        } catch (e: Exception) {
            android.util.Log.e("WorkViewModel", "Error cleaning up old reports: ${e.message}")
        }
    }

    fun generatePdfReport(context: Context): Uri? {
        return try {
            val fileName = "work_report_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Define paint styles
            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 24f
                isFakeBoldText = true
            }

            val headerPaint = Paint().apply {
                color = Color.BLACK
                textSize = 16f
                isFakeBoldText = true
            }

            val contentPaint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
            }

            val smallPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 10f
            }

            // Draw title
            var y = 50f
            canvas.drawText("Work Report", 40f, y, titlePaint)
            y += 40f

            // Draw entries
            _workEntries.value.forEach { entry ->
                // Check if we need a new page
                if (y > 750f) {
                    pdfDocument.finishPage(page)
                    val newPage = pdfDocument.startPage(pageInfo)
                    canvas = newPage.canvas
                    y = 50f
                }

                // Draw date header
                canvas.drawText(entry.date.toString(), 40f, y, headerPaint)
                y += 25f

                // Draw time range
                val timeText = "${entry.startTime} - ${entry.endTime}"
                canvas.drawText(timeText, 40f, y, contentPaint)
                y += 20f

                // Draw duration
                val duration = entry.getDuration()
                canvas.drawText("Duration: ${String.format("%.2f", duration)} hours", 40f, y, contentPaint)
                y += 20f

                // Draw comments
                canvas.drawText("Comments:", 40f, y, headerPaint)
                y += 20f
                canvas.drawText("EN: ${entry.commentEn}", 60f, y, contentPaint)
                y += 20f
                canvas.drawText("NL: ${entry.commentNl}", 60f, y, contentPaint)
                y += 20f

                // Draw photo if present
                entry.photoPath?.let { path ->
                    try {
                        val photoFile = File(path)
                        if (photoFile.exists()) {
                            val bitmap = BitmapFactory.decodeFile(path)
                            if (bitmap != null) {
                                // Scale down the photo to fit the page width
                                val scale = 200f / bitmap.width
                                val scaledWidth = bitmap.width * scale
                                val scaledHeight = bitmap.height * scale
                                
                                val matrix = Matrix().apply {
                                    postScale(scale, scale)
                                }
                                
                                canvas.drawBitmap(bitmap, matrix, Paint())
                                y += scaledHeight + 20f
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Add separator line
                canvas.drawLine(40f, y, 555f, y, Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 1f
                })
                y += 30f
            }

            pdfDocument.finishPage(page)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            // Clean up old reports after generating new one
            cleanupOldReports()

            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generateCsvReport(context: Context): Uri? {
        return try {
            val fileName = "work_report_${System.currentTimeMillis()}.csv"
            val file = File(context.cacheDir, fileName)

            file.printWriter(Charsets.UTF_8).use { out ->
                // Write header row
                out.println("Date,Start Time,End Time,Duration (hours),Material Cost,Comment (EN),Comment (NL)")
                
                // Write data rows
                _workEntries.value.forEach { entry ->
                    val duration = entry.getDuration()
                    
                    // Escape values that might contain commas or line breaks
                    val escapedCommentEn = entry.commentEn.replace("\"", "\"\"")
                    val escapedCommentNl = entry.commentNl.replace("\"", "\"\"")
                    
                    // Format the row with proper escaping
                    val row = listOf(
                        entry.date.toString(),
                        entry.startTime.toString(),
                        entry.endTime.toString(),
                        String.format("%.2f", duration),
                        "", // Placeholder for material cost
                        "\"$escapedCommentEn\"",
                        "\"$escapedCommentNl\""
                    ).joinToString(",")
                    
                    out.println(row)
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            // Clean up old reports after generating new one
            cleanupOldReports()

            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class FilterOptions(
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val showOvertime: Boolean = false,
    val showOnlyWithPhotos: Boolean = false
) 