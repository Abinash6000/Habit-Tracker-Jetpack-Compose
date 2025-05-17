package com.project.socialhabittracker.data.local.db

import android.content.Context
import com.project.socialhabittracker.data.local.db.habit_completion_db.HabitCompletionRepository
import com.project.socialhabittracker.data.local.db.habit_completion_db.OfflineHabitCompletionRepository
import com.project.socialhabittracker.data.local.db.habit_db.HabitRepository
import com.project.socialhabittracker.data.local.db.habit_db.OfflineHabitRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val habitCompletionRepository: HabitCompletionRepository
    val habitRepository: HabitRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [HabitRepository]
     */
    override val habitRepository: HabitRepository by lazy {
        OfflineHabitRepository(HabitTrackerDatabase.getHabitTrackerDatabase(context).habitDao())
    }
    /**
     * Implementation for [HabitCompletionRepository]
     */
    override val habitCompletionRepository: HabitCompletionRepository by lazy {
        OfflineHabitCompletionRepository(HabitTrackerDatabase.getHabitTrackerDatabase(context).habitCompletionDao())
    }
}