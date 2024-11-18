package com.project.socialhabittracker.data.db.habit_completion_db

import kotlinx.coroutines.flow.Flow

interface HabitCompletionRepository {
    suspend fun insertHabitCompletion(habitCompletion: HabitCompletion)

    suspend fun deleteHabitCompletion(habitId: Int)

    fun getCompletionDetailsStream(habitId: Int): Flow<List<HabitCompletion>>

    fun getAllCompletionDetailsStream(): Flow<List<HabitCompletion>>
}