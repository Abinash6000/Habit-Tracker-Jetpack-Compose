package com.project.socialhabittracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletionDao
import com.project.socialhabittracker.data.db.habit_db.Habit
import com.project.socialhabittracker.data.db.habit_db.HabitDao

@Database(entities = [Habit::class, HabitCompletion::class], version = 2)
abstract class HabitTrackerDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao

    companion object {
        @Volatile
        private var Instance: HabitTrackerDatabase? = null

        fun getHabitTrackerDatabase(context: Context): HabitTrackerDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, HabitTrackerDatabase::class.java, "habit_tracker_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}