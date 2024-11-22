package com.project.socialhabittracker.ui.habit_report

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletionRepository
import com.project.socialhabittracker.data.db.habit_db.HabitRepository
import com.project.socialhabittracker.ui.home.HabitInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HabitReportViewModel(
    savedStateHandle: SavedStateHandle,
    private val habitRepository: HabitRepository,
    private val habitCompletionRepository: HabitCompletionRepository
) : ViewModel() {
    var habitReportScore by mutableStateOf(HabitScores())
        private set
    var habitReportUiState by mutableStateOf(HabitInfo())
        private set

    val habitId: Int = checkNotNull(savedStateHandle[HabitReportDestination.habitIdArg])

    init {
        viewModelScope.launch {
            val habitInfoDeferred = async {
                habitRepository.getHabitStream(habitId).filterNotNull().first()
            }
            val habitCompletionDeferred = async {
                habitCompletionRepository.getCompletionDetailsStream(habitId).filterNotNull().first()
            }

            habitReportUiState = HabitInfo(habitInfoDeferred.await(), habitCompletionDeferred.await())

            habitReportScore = getScores(
                habitCompletions = habitReportUiState.habitCompletionDetails,
                target = habitReportUiState.habit.targetCount.toFloat(),
                isMeasurable = habitReportUiState.habit.type.equals("Measurable")
            )
        }
    }

    private fun getScores(habitCompletions: List<HabitCompletion>, target: Float, isMeasurable: Boolean): HabitScores {
        var perfectDays = 0
        var missedDays = 0
        val totalDays = habitCompletions.size
        var currentStreak = 0
        var bestStreak = 0
        var streakCount = 0
        var isRecentStreak = true

        for(hc in habitCompletions.reversed()) {
            if(isMeasurable && hc.progressValue.toFloat() >= target) {
                perfectDays++
                streakCount++
                if(isRecentStreak)
                    currentStreak = streakCount
                bestStreak = maxOf(streakCount, bestStreak)
            } else if(!isMeasurable && hc.isCompleted) {
                perfectDays++
                streakCount++
                if(isRecentStreak)
                    currentStreak = streakCount
                bestStreak = maxOf(streakCount, bestStreak)
            } else {
                missedDays++
                isRecentStreak = false
                streakCount = 0
            }
        }

        val percentage = (perfectDays.toDouble()/totalDays)*100
        Log.d("abcde", percentage.toString())
        return HabitScores(
            perfectDaysScore = perfectDays.toString(),
            currentStreakScore = currentStreak.toString(),
            bestStreakScore = bestStreak.toString(),
            percentageScore = String.format("%.2f", percentage),
            overallScore = "$perfectDays/$totalDays",
            missedDaysScore = missedDays.toString(),
        )
    }
}

data class HabitScores(
    val perfectDaysScore: String = "",
    val currentStreakScore: String = "",
    val bestStreakScore: String = "",
    val percentageScore: String = "",
    val overallScore: String = "",
    val missedDaysScore: String = "",
)
