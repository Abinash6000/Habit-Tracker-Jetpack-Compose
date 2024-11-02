package com.project.socialhabittracker.data.db.habit_completion_db

import kotlinx.coroutines.flow.Flow

interface HabitCompletionRepository {
    suspend fun insertHabitCompletion(habitCompletion: HabitCompletion)

//    suspend fun updateHabitCompletion(habitId: Int, date: Long, isCompleted: Boolean, progressValue: Float)

    suspend fun deleteHabitCompletion(habitId: Int)

    fun getAllCompletionDetailsStream(): Flow<List<HabitCompletion>>
}