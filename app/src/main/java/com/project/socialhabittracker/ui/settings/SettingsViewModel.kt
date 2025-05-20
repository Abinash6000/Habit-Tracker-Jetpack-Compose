package com.project.socialhabittracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.socialhabittracker.data.combined.CombinedHabitRepository
import com.project.socialhabittracker.data.remote.auth.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val combinedHabitRepository: CombinedHabitRepository
) : ViewModel() {

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val authViewModel = AuthViewModel()

    suspend fun onSyncFromCloud() {
            _loadingState.value = true
            combinedHabitRepository.syncFromCloud()
            _loadingState.value = false
    }

    suspend fun onSyncToCloud() {
            _loadingState.value = true
            combinedHabitRepository.syncToCloud()
            _loadingState.value = false
    }

    suspend fun onLogoutClick() {
            _loadingState.value = true
            combinedHabitRepository.syncToCloud()
            combinedHabitRepository.clearRoomData()
            authViewModel.logout()
            _loadingState.value = false
    }

    fun clearRoomData() {
        viewModelScope.launch {
            _loadingState.value = true
            combinedHabitRepository.clearRoomData()
            _loadingState.value = false
        }
    }
}