package com.project.socialhabittracker.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletionRepository
import com.project.socialhabittracker.data.db.habit_db.Habit
import com.project.socialhabittracker.data.db.habit_db.HabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(habitRepository: HabitRepository, habitCompletionRepository: HabitCompletionRepository) : ViewModel() {
    // Using a MutableStateFlow to hold the HomeUiState
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init {
        // Launch a coroutine in viewModelScope to fetch and update homeUiState
        viewModelScope.launch {
            habitRepository.getAllHabitsStream().collect { habits ->
                // Concurrently fetch completion details for each habit
                val habitInfoList = habits.map { habit ->
                    async {
                        val completionDetails = habitCompletionRepository.getAllCompletionDetailsStream(habit.id)
                            .firstOrNull() ?: emptyList()
                        HabitInfo(habit, completionDetails)
                    }
                }.awaitAll() // Await all async results to get the complete list

                // Update the HomeUiState with the fetched HabitInfo list
                _homeUiState.value = HomeUiState(habitInfoList)
            }
        }
    }
}

data class HomeUiState(
    val habitsList: List<HabitInfo> = listOf()
)

data class HabitInfo(
    val habit: Habit,
    val habitCompletionDetails: List<HabitCompletion>
)