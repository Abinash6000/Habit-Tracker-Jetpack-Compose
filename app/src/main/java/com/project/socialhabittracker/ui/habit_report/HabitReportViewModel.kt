package com.project.socialhabittracker.ui.habit_report

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.socialhabittracker.data.local.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.data.local.habit_completion_db.HabitCompletionRepository
import com.project.socialhabittracker.data.local.habit_db.HabitRepository
import com.project.socialhabittracker.ui.home.HabitInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

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

            val habitCompletions = habitCompletionDeferred.await()
            val allHabitCompletion = fillMissingHabitCompletions(
                habitId,
                habitCompletions,
                habitCompletions.first().date,
                habitCompletions.last().date
            )

            Log.d("abcdef", allHabitCompletion.toString())

            habitReportUiState = HabitInfo(habitInfoDeferred.await(), allHabitCompletion)

            habitReportScore = getScores(
                habitCompletions = habitReportUiState.habitCompletionDetails,
                target = habitReportUiState.habit.targetCount.toFloat(),
                isMeasurable = habitReportUiState.habit.type.equals("Measurable")
            )
        }
    }

    private fun fillMissingHabitCompletions(
        habitId: Int,
        habitCompletions: List<HabitCompletion>,
        minDate: Long,
        maxDate: Long
    ): List<HabitCompletion> {
        Log.d("abcdef", "minDate: $minDate, maxDate: $maxDate")
        val allHabitCompletion = mutableListOf<HabitCompletion>()

        // Normalize dates to midnight for comparison
        val existingDates = habitCompletions.associateBy {
            normalizeToMidnight(it.date)
        }

        var currentDate = normalizeToMidnight(minDate)

        while (currentDate <= maxDate) {
            val normalizedDate = normalizeToMidnight(currentDate)
            val completion = existingDates[normalizedDate]
            if (completion != null) {
                allHabitCompletion.add(completion)
            } else {
                allHabitCompletion.add(
                    HabitCompletion(
                        date = normalizedDate,
                        habitId = habitId,
                        isCompleted = false,
                        progressValue = "0",
                    )
                )
            }

            // Go to the next day (add 24h in millis)
            currentDate += 24 * 60 * 60 * 1000
        }


        return allHabitCompletion
    }

    // Normalize dates to midnight for comparison
    private fun normalizeToMidnight(dateInMillis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

//    private suspend fun getDatesBetween(minDateValue: Long, maxDateValue: Long): List<Long> = coroutineScope {
////        val pairOfDateRange =
////            async { habitCompletionRepository.getParticularMinMaxDateStream(habitId).filterNotNull().first() }
////        val maxDate =
////            async { habitCompletionRepository.getParticularMaxDateStream(habitId).filterNotNull().first() }
////        val minDate =
////            async { habitCompletionRepository.getParticularMinDateStream(habitId).filterNotNull().first() }
////        val existingDates = async {
////            habitCompletionRepository.getAllDatesStream(habitId).filterNotNull().first()
////        }
//
//        val cal = Calendar.getInstance()
//        val datesBetMaxAndMin = mutableListOf<Long>()
//
////        val minDateValue = minDate.await()
////        val maxDateValue = maxDate.await()
////        val existingDateSet = existingDates.await().toSet() // Convert to Set for faster lookup
//
//        if(minDateValue != 0L)
//            cal.timeInMillis = minDateValue // Start at minDate
//
//        while (cal.timeInMillis <= maxDateValue) {
//            val currentDate = cal.timeInMillis
////            if (currentDate !in existingDateSet) {
//            datesBetMaxAndMin.add(currentDate)
////            }
//
//            // Increment by one day
//            cal.add(Calendar.DAY_OF_MONTH, 1)
//        }
//
//        return@coroutineScope datesBetMaxAndMin
//    }

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
