package com.example.timetracker.ui.work

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetracker.data.WorkEntry
import com.example.timetracker.data.WorkEntryDao
import com.example.timetracker.datastore.PreferencesManager
import com.example.timetracker.utils.LocaleUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val workEntryDao: WorkEntryDao,
    private val preferencesManager: PreferencesManager,
    private val localeUtils: LocaleUtils,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _workEntries = MutableStateFlow<List<WorkEntry>>(emptyList())
    val workEntries: StateFlow<List<WorkEntry>> = _workEntries.asStateFlow()

    private val _weeklyHours = MutableStateFlow(40)
    val weeklyHours: StateFlow<Int> = _weeklyHours.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            workEntryDao.getAllEntries().collect { entries ->
                _workEntries.value = entries
            }
        }

        viewModelScope.launch {
            preferencesManager.weeklyHoursFlow.collect { hours ->
                _weeklyHours.value = hours
            }
        }

        viewModelScope.launch {
            preferencesManager.selectedLanguageFlow.collect { language ->
                _selectedLanguage.value = language
                localeUtils.setLocale(context, language)
            }
        }
    }

    fun addWorkEntry(entry: WorkEntry) {
        viewModelScope.launch {
            workEntryDao.insert(entry)
        }
    }

    fun updateWorkEntry(entry: WorkEntry) {
        viewModelScope.launch {
            workEntryDao.update(entry)
        }
    }

    fun deleteWorkEntry(entry: WorkEntry) {
        viewModelScope.launch {
            workEntryDao.delete(entry)
        }
    }

    fun setWeeklyHours(hours: Int) {
        viewModelScope.launch {
            preferencesManager.setWeeklyHours(hours)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            preferencesManager.setSelectedLanguage(language)
        }
    }

    fun generatePdfReport(): String {
        // TODO: Implement PDF generation
        return ""
    }

    fun generateCsvReport(): String {
        // TODO: Implement CSV generation
        return ""
    }
} 