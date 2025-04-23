package com.example.timetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetracker.utils.UiMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    protected val uiMessageManager: UiMessageManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "Error in coroutine")
        handleError(throwable)
    }

    protected fun handleError(throwable: Throwable) {
        viewModelScope.launch {
            _isLoading.value = false
            uiMessageManager.showError(throwable.message ?: "An unknown error occurred")
        }
    }

    protected fun <T> safeCall(
        block: suspend () -> T,
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = { handleError(it) }
    ) {
        viewModelScope.launch(exceptionHandler) {
            try {
                _isLoading.value = true
                val result = block()
                onSuccess(result)
            } catch (e: Exception) {
                onError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    protected fun showSuccess(message: String) {
        uiMessageManager.showSuccess(message)
    }

    protected fun showError(message: String) {
        uiMessageManager.showError(message)
    }

    protected fun showInfo(message: String) {
        uiMessageManager.showInfo(message)
    }

    val uiMessage: StateFlow<String?> = uiMessageManager.message.asStateFlow()

    protected fun logError(message: String, throwable: Throwable? = null) {
        Timber.e(throwable, message)
        uiMessageManager.emitMessage(message)
    }
}

class UiMessageManager {
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun emitMessage(message: String) {
        _message.value = message
    }

    fun clearMessage() {
        _message.value = null
    }
} 