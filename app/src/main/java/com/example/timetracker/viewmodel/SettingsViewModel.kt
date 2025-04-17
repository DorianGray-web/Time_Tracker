package com.example.timetracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetracker.data.preferences.ThemePreferences
import com.example.timetracker.data.preferences.LanguagePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferences: ThemePreferences,
    private val languagePreferences: LanguagePreferences,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            themePreferences.themeMode.collect { mode ->
                _themeMode.value = mode
            }
        }
        viewModelScope.launch {
            languagePreferences.language.collect { language ->
                _selectedLanguage.value = language
            }
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.updateThemeMode(mode)
            _snackbarMessage.value = "Theme updated"
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            languagePreferences.updateLanguage(language)
            _snackbarMessage.value = "Language updated"
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
} 