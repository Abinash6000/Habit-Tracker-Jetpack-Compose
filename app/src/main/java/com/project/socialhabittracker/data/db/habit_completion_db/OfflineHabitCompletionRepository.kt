package com.project.socialhabittracker.data.db.habit_completion_db

import kotlinx.coroutines.flow.Flow

class OfflineHabitCompletionRepository(private val habitCompletionDao: HabitCompletionDao) : HabitCompletionRepository {
    override suspend fun insertHabitCompletion(habitCompletion: HabitCompletion) = habitCompletionDao.insert(habitCompletion)
//    override suspend fun updateHabitCompletion(
//        habitId: Int,
//        date: Long,
//        isCompleted: Boolean,
//        progressValue: Float
//    ) = habitCompletionDao.update(habitId, date, isCompleted, progressValue)

    override suspend fun deleteHabitCompletion(habitId: Int) = habitCompletionDao.deleteAllByHabitId(habitId)

    override fun getAllCompletionDetailsStream(): Flow<List<HabitCompletion>> = habitCompletionDao.getAllCompletionDetails()

}