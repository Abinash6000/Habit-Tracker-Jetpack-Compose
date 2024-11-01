package com.project.socialhabittracker.ui.add_habit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.project.socialhabittracker.data.db.habit_db.Habit
import com.project.socialhabittracker.data.db.habit_db.HabitRepository

class AddHabitViewModel(private val habitRepository: HabitRepository) : ViewModel() {
    var addHabitUiState by mutableStateOf(AddHabitUiState())
        private set

    private fun validateInput(uiState: Habit = addHabitUiState.habit): Boolean {
        return with(uiState) {
            name.isNotBlank() && type.isNotBlank()
        }
    }

    fun updateAddHabitUiState(habit: Habit) {
        addHabitUiState =
            AddHabitUiState(habit = habit, isEntryValid = validateInput(habit))
    }

    suspend fun saveHabit() {
        if(validateInput()) {
            habitRepository.insertHabit(addHabitUiState.habit)
        }
    }
}

data class AddHabitUiState(
    val habit: Habit = Habit(),
    val isEntryValid: Boolean = false
)