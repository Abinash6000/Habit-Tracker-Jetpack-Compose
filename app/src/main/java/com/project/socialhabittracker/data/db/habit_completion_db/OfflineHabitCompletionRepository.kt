package com.project.socialhabittracker.data.db.habit_completion_db

import kotlinx.coroutines.flow.Flow

class OfflineHabitCompletionRepository(private val habitCompletionDao: HabitCompletionDao) : HabitCompletionRepository {
    override suspend fun insertHabitCompletion(habitCompletion: HabitCompletion) = habitCompletionDao.insert(habitCompletion)

    override suspend fun updateHabitCompletion(habitCompletion: HabitCompletion) = habitCompletionDao.update(habitCompletion)

    override suspend fun deleteHabitCompletion(habitCompletion: HabitCompletion) = habitCompletionDao.delete(habitCompletion)

    override fun getAllCompletionDetailsStream(habitId: Int): Flow<List<HabitCompletion>> = habitCompletionDao.getAllCompletionDetails(habitId)

}