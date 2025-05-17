package com.project.socialhabittracker.data.local.db.habit_completion_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.project.socialhabittracker.data.local.db.habit_db.Habit
import com.project.socialhabittracker.ui.home.convertToDateMonthYear
import com.project.socialhabittracker.ui.home.convertToMillis
import java.util.Calendar

@Entity(
    tableName = "habit_completion",
    foreignKeys = [ForeignKey(
        entity = Habit::class,
        parentColumns = ["id"],
        childColumns = ["habit_id"],
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys = ["habit_id", "date"]
)
data class HabitCompletion(
    @ColumnInfo(name = "habit_id") val habitId: Int, // Foreign key to Habit table
    @ColumnInfo(name = "date") val date: Long, // Store date as timestamp
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false, // For "yes_no" habits
    @ColumnInfo(name = "progress_value") val progressValue: String = "0" // For "measurable" habits
)