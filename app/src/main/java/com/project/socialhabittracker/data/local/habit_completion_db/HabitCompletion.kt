package com.project.socialhabittracker.data.local.habit_completion_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.google.firebase.firestore.PropertyName
import com.project.socialhabittracker.data.local.habit_db.Habit

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
    @ColumnInfo(name = "habit_id") val habitId: Int = 0, // Foreign key to Habit table
    @ColumnInfo(name = "date") val date: Long = 0, // Store date as timestamp
    @get:PropertyName("isCompleted")
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false, // For "yes_no" habits
    @ColumnInfo(name = "progress_value") val progressValue: String = "0" // For "measurable" habits
)