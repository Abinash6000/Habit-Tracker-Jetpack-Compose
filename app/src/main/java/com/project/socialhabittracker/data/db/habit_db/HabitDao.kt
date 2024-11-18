package com.project.socialhabittracker.data.db.habit_db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Update
    suspend fun update(habit: Habit)

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabit(id: Int): Flow<Habit>

    @Query("SELECT * FROM habits ORDER BY name ASC")
    fun getAllHabits(): Flow<List<Habit>>
}