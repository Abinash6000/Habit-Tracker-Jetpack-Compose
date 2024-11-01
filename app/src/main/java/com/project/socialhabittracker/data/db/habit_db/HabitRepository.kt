package com.project.socialhabittracker.data.db.habit_db

import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    suspend fun insertHabit(habit: Habit)

    suspend fun updateHabit(habit: Habit)

    suspend fun deleteHabit(habit: Habit)

    fun getAllHabitsStream(): Flow<List<Habit>>
}