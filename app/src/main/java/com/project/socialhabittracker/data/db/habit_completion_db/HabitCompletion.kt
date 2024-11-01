package com.project.socialhabittracker.data.db.habit_completion_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.project.socialhabittracker.data.db.habit_db.Habit

@Entity(
    tableName = "habit_completion",
    foreignKeys = [ForeignKey(
        entity = Habit::class,
        parentColumns = ["id"],
        childColumns = ["habit_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "habit_id") val habitId: Int, // Foreign key to Habit table
    @ColumnInfo(name = "date") val date: Long, // Store date as timestamp
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean?, // For "yes_no" habits
    @ColumnInfo(name = "progress_value") val progressValue: Float? = null // For "measurable" habits
)