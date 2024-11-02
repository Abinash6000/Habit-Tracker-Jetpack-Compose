package com.project.socialhabittracker.data.db.habit_completion_db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habitCompletion: HabitCompletion)

//    @Query("UPDATE habit_completion SET is_completed = :isCompleted, progress_value = :progressValue " +
//            "WHERE habit_id = :habitId AND date = :date")
//    suspend fun update(habitId: Int, date: Long, isCompleted: Boolean, progressValue: Float)

    @Query("DELETE FROM habit_completion WHERE habit_id = :habitId")
    suspend fun deleteAllByHabitId(habitId: Int)

    @Query("SELECT * FROM habit_completion")
    fun getAllCompletionDetails(): Flow<List<HabitCompletion>>
}