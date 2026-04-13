package com.example.androidsync.ui.screen.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidsync.data.remote.auth.GoogleAccount
import com.example.androidsync.data.remote.auth.GoogleAuthManager
import com.example.androidsync.data.remote.drive.DriveServiceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val account: GoogleAccount? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class SettingsViewModel(
    private val authManager: GoogleAuthManager,
    private val driveServiceProvider: DriveServiceProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(account = authManager.currentAccount)
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun signIn(activityContext: Context, serverClientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val account = authManager.signIn(activityContext, serverClientId)
                driveServiceProvider.getDriveService(account.email)
                _uiState.value = _uiState.value.copy(
                    account = account,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Sign-in failed",
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authManager.signOut()
            driveServiceProvider.clearService()
            _uiState.value = SettingsUiState()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
