package com.project.socialhabittracker.data.db.habit_completion_db

import kotlinx.coroutines.flow.Flow

interface HabitCompletionRepository {
    suspend fun insertHabitCompletion(habitCompletion: HabitCompletion)

    suspend fun updateHabitCompletion(habitCompletion: HabitCompletion)

    suspend fun deleteHabitCompletion(habitCompletion: HabitCompletion)

    fun getAllCompletionDetailsStream(habitId: Int): Flow<List<HabitCompletion>>
}