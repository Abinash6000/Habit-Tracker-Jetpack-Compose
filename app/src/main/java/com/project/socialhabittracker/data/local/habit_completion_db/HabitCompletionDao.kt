package com.project.socialhabittracker.data.local.habit_completion_db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.project.socialhabittracker.data.local.habit_db.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habitCompletion: HabitCompletion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletions(habitCompletions: List<HabitCompletion>)

    // delete a completions of particular habit -> maybe not required because auto deleted because this is foreign table
    @Query("DELETE FROM habit_completion WHERE habit_id = :habitId")
    suspend fun deleteAllByHabitId(habitId: Int)

    // get completions of particular habit
    @Query("SELECT * FROM habit_completion WHERE habit_id = :habitId ORDER BY date")
    fun getCompletionDetails(habitId: Int): Flow<List<HabitCompletion>>

    // get all completions
    @Query("SELECT * FROM habit_completion")
    fun getAllCompletionDetails(): Flow<List<HabitCompletion>>

    // get max date of a particular habit
    @Query("SELECT MAX(date) FROM habit_completion WHERE habit_id = :habitId")
    fun getParticularMaxDate(habitId: Int): Flow<Long>

    // get min date of a particular habit
    @Query("SELECT MIN(date) FROM habit_completion WHERE habit_id = :habitId")
    fun getParticularMinDate(habitId: Int): Flow<Long>

    // get min and max date of all completions
    @Query("SELECT MIN(date) AS minDate, MAX(date) AS maxDate FROM habit_completion")
    fun getMinMaxDate(): Flow<DateRange>

    // get completion according to date
    @Query("SELECT * FROM habit_completion WHERE date = :date")
    fun getCompletionsForDate(date: Long): Flow<List<HabitCompletion>>

    @Query("SELECT date FROM habit_completion WHERE habit_id = :habitId ORDER BY date ASC")
    fun getAllDates(habitId: Int): Flow<List<Long>>

    @Query("DELETE FROM habit_completion")
    suspend fun clearAllCompletions()
}

data class DateRange(val maxDate: Long, val minDate: Long)