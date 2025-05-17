package com.project.socialhabittracker.ui.overall_report

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.socialhabittracker.data.local.db.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.data.local.db.habit_completion_db.HabitCompletionRepository
import com.project.socialhabittracker.data.local.db.habit_db.Habit
import com.project.socialhabittracker.data.local.db.habit_db.HabitRepository
import com.project.socialhabittracker.ui.habit_report.HabitScores
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class OverallReportViewModel(
    private val habitRepository: HabitRepository,
    private val habitCompletionRepository: HabitCompletionRepository
) : ViewModel() {
    var reportScore: HabitScores by mutableStateOf(HabitScores())
        private set

    init {
        viewModelScope.launch {
            reportScore = getScores()
        }
    }

    private suspend fun getScores(): HabitScores = coroutineScope {
        var perfectDays = 0
        var missedDays = 0
        var totalDays = 0
        var currentStreak = 0
        var bestStreak = 0
        var streakCount = 0
        var isRecentStreak = true

        val datesBetMaxAndMin = getDatesBetween()
        val allHabits = async { habitRepository.getAllHabitsStream().filterNotNull().first() }.await()
        var completionsForDate: List<HabitCompletion>

        for(date in datesBetMaxAndMin) {
            completionsForDate = async { habitCompletionRepository.getCompletionsForDateStream(date).filterNotNull().first() }.await()

            var perfectDay = true
            for(habitCompletion in completionsForDate) {
                val currHabit = allHabits.filter { habit ->
                    habit.id == habitCompletion.habitId
                }[0]

                if(currHabit.type.equals("measurable", true)) {
                    if(habitCompletion.progressValue < currHabit.targetCount) perfectDay = false
                } else {
                    if(!habitCompletion.isCompleted) perfectDay = false
                }
            }

            if(perfectDay) {
                perfectDays++
                streakCount++
                if(isRecentStreak)
                    currentStreak++
            } else {
                streakCount = 0
                missedDays++
                isRecentStreak = false
            }
            bestStreak = Math.max(streakCount, bestStreak)
            totalDays++
        }

        val percentage = (perfectDays.toDouble()/totalDays)*100
        return@coroutineScope HabitScores(
            perfectDaysScore = perfectDays.toString(),
            currentStreakScore = currentStreak.toString(),
            bestStreakScore = bestStreak.toString(),
            percentageScore = String.format("%.2f", percentage),
            overallScore = "$perfectDays/$totalDays",
            missedDaysScore = missedDays.toString(),
        )
    }

    private suspend fun getDatesBetween(): List<Long> = coroutineScope {
        val dateRange =
            async { habitCompletionRepository.getMinMaxDateStream().filterNotNull().first() }

        val cal = Calendar.getInstance()
        val datesBetMaxAndMin = mutableListOf<Long>()

        val minDateValue = dateRange.await().minDate
        val maxDateValue = dateRange.await().maxDate

        if(minDateValue != 0L)
            cal.timeInMillis = minDateValue // Start at minDate

        while (cal.timeInMillis <= maxDateValue) {
            val currentDate = cal.timeInMillis
            datesBetMaxAndMin.add(currentDate)

            // Increment by one day
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        return@coroutineScope datesBetMaxAndMin.sortedDescending()
    }
}