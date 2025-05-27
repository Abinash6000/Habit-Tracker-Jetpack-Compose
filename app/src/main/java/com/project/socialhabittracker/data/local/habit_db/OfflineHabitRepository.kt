package com.project.socialhabittracker.data.local.habit_db

import kotlinx.coroutines.flow.Flow

class OfflineHabitRepository(private val habitDao: HabitDao) : HabitRepository {
    override suspend fun insertHabit(habit: Habit) = habitDao.insert(habit)

    override suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

    override suspend fun deleteHabit(id: Int) = habitDao.delete(id)

    override fun getHabitStream(id: Int): Flow<Habit> = habitDao.getHabit(id)

    override fun getAllHabitsStream(): Flow<List<Habit>> = habitDao.getAllHabits()
}