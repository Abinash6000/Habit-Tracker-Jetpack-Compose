package com.project.socialhabittracker.data.db.habit_db

import kotlinx.coroutines.flow.Flow

class OfflineHabitRepository(private val habitDao: HabitDao) : HabitRepository {
    override suspend fun insertHabit(habit: Habit) = habitDao.insert(habit)

    override suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

    override suspend fun deleteHabit(habit: Habit) = habitDao.delete(habit)

    override fun getAllHabitsStream(): Flow<List<Habit>> = habitDao.getAllHabits()
}