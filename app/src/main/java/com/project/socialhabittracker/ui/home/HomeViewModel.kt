package com.project.socialhabittracker.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletionRepository
import com.project.socialhabittracker.data.db.habit_db.Habit
import com.project.socialhabittracker.data.db.habit_db.HabitRepository
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

class HomeViewModel(private val habitRepository: HabitRepository, private val habitCompletionRepository: HabitCompletionRepository) : ViewModel() {
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

        // Launch another coroutine to react to changes in homeUiState
        viewModelScope.launch {
            homeUiState.collect { uiState ->
                uiState.habitsList.forEach { habitInfo ->
                    fillMissingDates(habitInfo.habit.id)
                }
            }
        }
    }

    fun upsert(habitCompletion: HabitCompletion) {
        viewModelScope.launch {
            Log.d("minDate", habitCompletion.toString())
            habitCompletionRepository.insertHabitCompletion(habitCompletion)
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habitId)
        }
    }

    private suspend fun fillMissingDates(habitId: Int) {
        val datesBetMaxAndMin = getDatesBetween(habitId)

        viewModelScope.launch {
            datesBetMaxAndMin.forEach { date ->
                habitCompletionRepository.insertHabitCompletion(
                    HabitCompletion(habitId = habitId, date = date, progressValue = "0", isCompleted = false)
                )
            }
        }
    }

    private suspend fun getDatesBetween(habitId: Int): List<Long> = coroutineScope {
        val maxDate =
            async { habitCompletionRepository.getMaxDateStream().filterNotNull().first() }
        val minDate =
            async { habitCompletionRepository.getMinDateStream().filterNotNull().first() }
        val existingDates = async {
            habitCompletionRepository.getAllDatesStream(habitId).filterNotNull().first()
        }

        val cal = Calendar.getInstance()
        val datesBetMaxAndMin = mutableListOf<Long>()

        val maxDateValue = maxDate.await()
        var minDateValue = minDate.await()
        Log.d("abcde", "maxDateValue: $maxDateValue")
        Log.d("abcde", "minDateValue: $minDateValue")
        val existingDateSet = existingDates.await().toSet() // Convert to Set for faster lookup

        if(minDateValue != 0L)
            cal.timeInMillis = minDateValue // Start at minDate

        while (cal.timeInMillis <= maxDateValue) {
            val currentDate = cal.timeInMillis
            if (currentDate !in existingDateSet) {
                datesBetMaxAndMin.add(currentDate)
            }

            // Increment by one day
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        return@coroutineScope datesBetMaxAndMin
    }
}

data class HomeUiState(
    val habitsList: List<HabitInfo> = listOf()
)

data class HabitInfo(
    val habit: Habit = Habit(),
    val habitCompletionDetails: List<HabitCompletion> = listOf()
)