package com.project.socialhabittracker.data.local.habit_completion_db

import kotlinx.coroutines.flow.Flow

interface HabitCompletionRepository {
    suspend fun insertHabitCompletion(habitCompletion: HabitCompletion)

    suspend fun deleteHabitCompletion(habitId: Int)

    fun getCompletionDetailsStream(habitId: Int): Flow<List<HabitCompletion>>

    fun getAllCompletionDetailsStream(): Flow<List<HabitCompletion>>

    fun getCompletionsForDateStream(date: Long): Flow<List<HabitCompletion>>

    fun getMinMaxDateStream(): Flow<DateRange>

    fun getParticularMaxDateStream(habitId: Int): Flow<Long>

    fun getParticularMinDateStream(habitId: Int): Flow<Long>

    fun getAllDatesStream(habitId: Int): Flow<List<Long>>
}