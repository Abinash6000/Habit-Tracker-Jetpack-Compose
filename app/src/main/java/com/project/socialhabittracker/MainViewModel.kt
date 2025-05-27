package com.project.socialhabittracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.socialhabittracker.data.preferences.DataStoreInstance
import com.project.socialhabittracker.data.preferences.ThemeRepository
import com.project.socialhabittracker.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel(){

    private val _appTheme = MutableStateFlow("Blue")
    val appTheme: StateFlow<String> = _appTheme

    init {
        getTheme()
    }

    fun saveTheme(theme: AppTheme) {
        viewModelScope.launch {
            themeRepository.saveTheme(theme)
        }
    }

    private fun getTheme() {
        viewModelScope.launch {
            themeRepository.getTheme().collect { theme ->
                _appTheme.value = theme
            }
        }
    }
}