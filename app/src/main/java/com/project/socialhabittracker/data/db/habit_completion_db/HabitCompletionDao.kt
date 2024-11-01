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

    @Update
    suspend fun update(habitCompletion: HabitCompletion)

    @Delete
    suspend fun delete(habitCompletion: HabitCompletion)

    @Query("SELECT * FROM habit_completion WHERE habit_id == :habitId")
    fun getAllCompletionDetails(habitId: Int): Flow<List<HabitCompletion>>
}