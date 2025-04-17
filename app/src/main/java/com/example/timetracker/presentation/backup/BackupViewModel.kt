package com.example.timetracker.presentation.backup

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetracker.domain.model.WorkEntry
import com.example.timetracker.domain.repository.WorkEntryRepository
import com.example.timetracker.utils.BackupUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    application: Application,
    private val repository: WorkEntryRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<BackupUiState>(BackupUiState.Idle)
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    fun createBackup() {
        viewModelScope.launch {
            try {
                _uiState.value = BackupUiState.Loading
                val entries = repository.getAllEntries()
                val backupFile = BackupUtils.createBackup(getApplication(), entries)
                _uiState.value = BackupUiState.Success(backupFile.absolutePath)
            } catch (e: Exception) {
                Timber.e(e, "Failed to create backup")
                _uiState.value = BackupUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun restoreFromBackup(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = BackupUiState.Loading
                val entries = BackupUtils.restoreFromBackup(getApplication(), uri)
                
                // Clear existing entries
                repository.getAllEntries().forEach { entry ->
                    repository.deleteEntry(entry.id)
                }
                
                // Restore entries
                entries.forEach { entry ->
                    repository.addEntry(entry)
                }
                
                _uiState.value = BackupUiState.RestoreSuccess
            } catch (e: Exception) {
                Timber.e(e, "Failed to restore from backup")
                _uiState.value = BackupUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _uiState.value = BackupUiState.Idle
    }
}

sealed class BackupUiState {
    object Idle : BackupUiState()
    object Loading : BackupUiState()
    data class Success(val filePath: String) : BackupUiState()
    object RestoreSuccess : BackupUiState()
    data class Error(val message: String) : BackupUiState()
} 