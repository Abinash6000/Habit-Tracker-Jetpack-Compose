package com.project.socialhabittracker.data.db.habit_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "note") val note: String = "",
    @ColumnInfo(name = "unit") val unit: String = "Units",
    @ColumnInfo(name = "target_count") val targetCount: Int = 0,
    @ColumnInfo(name = "type") val type: String = "Yes or No" // "yes_no" or "measurable"
)
