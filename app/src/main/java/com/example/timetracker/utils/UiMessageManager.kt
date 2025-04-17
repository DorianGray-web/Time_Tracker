package com.example.timetracker.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.timetracker.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

sealed class UiMessage {
    data class Error(val message: String) : UiMessage()
    data class Success(val message: String) : UiMessage()
    data class Info(val message: String) : UiMessage()
}

class UiMessageManager {
    private val _message = MutableStateFlow<UiMessage?>(null)
    val message: StateFlow<UiMessage?> = _message.asStateFlow()

    fun showError(message: String) {
        Timber.e(message)
        _message.value = UiMessage.Error(message)
    }

    fun showSuccess(message: String) {
        Timber.i(message)
        _message.value = UiMessage.Success(message)
    }

    fun showInfo(message: String) {
        Timber.d(message)
        _message.value = UiMessage.Info(message)
    }

    fun clearMessage() {
        _message.value = null
    }
}

@Composable
fun rememberUiMessageManager(): UiMessageManager {
    return remember { UiMessageManager() }
}

@Composable
fun UiMessageEffect(
    message: UiMessage?,
    onMessageShown: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(message) {
        message?.let {
            when (it) {
                is UiMessage.Error -> {
                    // Show error message
                    onMessageShown()
                }
                is UiMessage.Success -> {
                    // Show success message
                    onMessageShown()
                }
                is UiMessage.Info -> {
                    // Show info message
                    onMessageShown()
                }
            }
        }
    }
} 