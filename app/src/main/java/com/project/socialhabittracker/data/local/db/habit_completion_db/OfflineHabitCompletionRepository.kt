package com.project.socialhabittracker.data.local.db.habit_completion_db

import kotlinx.coroutines.flow.Flow

class OfflineHabitCompletionRepository(private val habitCompletionDao: HabitCompletionDao) :
    HabitCompletionRepository {
    override suspend fun insertHabitCompletion(habitCompletion: HabitCompletion) = habitCompletionDao.insert(habitCompletion)

    override suspend fun deleteHabitCompletion(habitId: Int) = habitCompletionDao.deleteAllByHabitId(habitId)

    override fun getCompletionDetailsStream(habitId: Int): Flow<List<HabitCompletion>> = habitCompletionDao.getCompletionDetails(habitId)

    override fun getAllCompletionDetailsStream(): Flow<List<HabitCompletion>> = habitCompletionDao.getAllCompletionDetails()

    override fun getCompletionsForDateStream(date: Long): Flow<List<HabitCompletion>> = habitCompletionDao.getCompletionsForDate(date)

    override fun getMinMaxDateStream(): Flow<DateRange> = habitCompletionDao.getMinMaxDate()

    override fun getParticularMaxDateStream(habitId: Int): Flow<Long> = habitCompletionDao.getParticularMaxDate(habitId)

    override fun getParticularMinDateStream(habitId: Int): Flow<Long> = habitCompletionDao.getParticularMinDate(habitId)

    override fun getAllDatesStream(habitId: Int): Flow<List<Long>> = habitCompletionDao.getAllDates(habitId)

}