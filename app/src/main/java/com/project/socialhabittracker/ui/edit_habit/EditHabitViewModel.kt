package com.project.socialhabittracker.ui.edit_habit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.socialhabittracker.data.local.habit_db.Habit
import com.project.socialhabittracker.data.local.habit_db.HabitRepository
import com.project.socialhabittracker.ui.habit_report.HabitReportDestination
import com.project.socialhabittracker.ui.home.HabitInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditHabitViewModel(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository
) : ViewModel() {
    var editHabitUiState by mutableStateOf(Habit())
        private set

    private val habitId: Int = checkNotNull(savedStateHandle[HabitReportDestination.habitIdArg])

    init {
        viewModelScope.launch {
            val habitDeferred = async {
                habitRepository.getHabitStream(habitId).filterNotNull().first()
            }

            editHabitUiState = habitDeferred.await()
        }
    }

    fun validateInput(uiState: Habit = editHabitUiState): Boolean {
        return with(uiState) {
            name.isNotBlank() && type.isNotBlank()
        }
    }

    suspend fun saveHabit() {
        if(validateInput()) {
            habitRepository.updateHabit(editHabitUiState)
        }
    }

    fun updateEditHabitUiState(habit: Habit) {
        editHabitUiState = habit
    }
}