package com.project.socialhabittracker.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.project.socialhabittracker.data.combined.CombinedHabitRepository
import com.project.socialhabittracker.data.local.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.data.local.habit_completion_db.HabitCompletionRepository
import com.project.socialhabittracker.data.local.habit_db.Habit
import com.project.socialhabittracker.data.local.habit_db.HabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(
    private val habitRepository: HabitRepository,
    private val habitCompletionRepository: HabitCompletionRepository,
    private val combinedHabitRepository: CombinedHabitRepository
) : ViewModel() {
    // Using a MutableStateFlow to hold the HomeUiState
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init {
        // Launch a coroutine to fetch and update homeUiState
        viewModelScope.launch {
            habitRepository.getAllHabitsStream().combine(habitCompletionRepository.getAllCompletionDetailsStream()) { habits, completions ->
                habits.map { habit ->
                    val completionDetails = completions.filter { it.habitId == habit.id }
                    HabitInfo(habit, completionDetails)
                }
            }.collect { habitInfoList ->
                _homeUiState.value = HomeUiState(habitInfoList)
            }
        }
    }

    fun upsert(habitCompletion: HabitCompletion) {
        viewModelScope.launch {
            habitCompletionRepository.insertHabitCompletion(habitCompletion)
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            combinedHabitRepository.deleteHabitWithCompletions(habitId)
        }
    }


}

data class HomeUiState(
    val habitsList: List<HabitInfo> = listOf()
)

data class HabitInfo(
    val habit: Habit = Habit(),
    val habitCompletionDetails: List<HabitCompletion> = listOf()
)