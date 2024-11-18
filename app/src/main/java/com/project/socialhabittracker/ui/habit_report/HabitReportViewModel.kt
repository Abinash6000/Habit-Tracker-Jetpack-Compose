package com.project.socialhabittracker.ui.habit_report

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletionRepository
import com.project.socialhabittracker.data.db.habit_db.HabitRepository
import com.project.socialhabittracker.ui.home.HabitInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HabitReportViewModel(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository,
    private val habitCompletionRepository: HabitCompletionRepository
) : ViewModel() {
    var habitReportUiState by mutableStateOf(HabitInfo())
        private set

    private val habitId: Int = checkNotNull(savedStateHandle[HabitReportDestination.habitIdArg])

    init {
        viewModelScope.launch {
            habitReportUiState = HabitInfo(habitRepository.getHabitStream(habitId)
                .filterNotNull()
                .first(), habitCompletionRepository.getCompletionDetailsStream(habitId)
                .filterNotNull()
                .first()
            )
        }
    }
}