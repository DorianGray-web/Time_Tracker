package com.example.timetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetracker.model.TimeEntry
import com.example.timetracker.repository.TimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val repository: TimeRepository
) : ViewModel() {

    private val _timeEntries = MutableStateFlow<List<TimeEntry>>(emptyList())
    val timeEntries: StateFlow<List<TimeEntry>> = _timeEntries.asStateFlow()

    private val _currentTimer = MutableStateFlow<TimeEntry?>(null)
    val currentTimer: StateFlow<TimeEntry?> = _currentTimer.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllTimeEntries().collect { entries ->
                _timeEntries.value = entries
            }
        }
    }

    fun startTimer(description: String) {
        viewModelScope.launch {
            repository.startTimer(description)
        }
    }

    fun stopTimer(entryId: Long) {
        viewModelScope.launch {
            repository.stopTimer(entryId)
        }
    }
} 