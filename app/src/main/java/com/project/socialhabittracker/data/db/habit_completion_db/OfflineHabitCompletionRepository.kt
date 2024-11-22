package com.project.socialhabittracker.data.db.habit_completion_db

import kotlinx.coroutines.flow.Flow

class OfflineHabitCompletionRepository(private val habitCompletionDao: HabitCompletionDao) : HabitCompletionRepository {
    override suspend fun insertHabitCompletion(habitCompletion: HabitCompletion) = habitCompletionDao.insert(habitCompletion)

    override suspend fun deleteHabitCompletion(habitId: Int) = habitCompletionDao.deleteAllByHabitId(habitId)

    override fun getCompletionDetailsStream(habitId: Int): Flow<List<HabitCompletion>> = habitCompletionDao.getCompletionDetails(habitId)

    override fun getAllCompletionDetailsStream(): Flow<List<HabitCompletion>> = habitCompletionDao.getAllCompletionDetails()
    override fun getMaxDateStream(): Flow<Long> = habitCompletionDao.getMaxDate()

    override fun getMinDateStream(): Flow<Long> = habitCompletionDao.getMinDate()

    override fun getAllDatesStream(habitId: Int): Flow<List<Long>> = habitCompletionDao.getAllDates(habitId)

}