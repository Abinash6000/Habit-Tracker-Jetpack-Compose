package com.project.socialhabittracker.data.combined

import com.project.socialhabittracker.data.local.db.habit_db.Habit

interface CombinedHabitRepository {
//    suspend fun addHabit(habit: Habit)
//    suspend fun completeHabit(habitId: Int, date: Long, isCompleted: Boolean, progressValue: String)
    suspend fun syncFromCloud()
    suspend fun syncToCloud()
    suspend fun deleteHabitWithCompletions(habitId: Int)
    suspend fun clearRoomData()
}