package com.project.socialhabittracker

import android.content.Context
import com.project.socialhabittracker.data.combined.CombinedHabitRepository
import com.project.socialhabittracker.data.combined.CombinedHabitRepositoryImpl
import com.project.socialhabittracker.data.local.HabitTrackerDatabase
import com.project.socialhabittracker.data.local.habit_completion_db.HabitCompletionRepository
import com.project.socialhabittracker.data.local.habit_completion_db.OfflineHabitCompletionRepository
import com.project.socialhabittracker.data.local.habit_db.HabitRepository
import com.project.socialhabittracker.data.local.habit_db.OfflineHabitRepository
import com.project.socialhabittracker.data.preferences.ThemeRepository
import com.project.socialhabittracker.data.remote.firestore.FirestoreService

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val habitCompletionRepository: HabitCompletionRepository
    val habitRepository: HabitRepository
    val combinedHabitRepository: CombinedHabitRepository
    val themeRepository: ThemeRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val habitRepository: HabitRepository by lazy {
        OfflineHabitRepository(HabitTrackerDatabase.getHabitTrackerDatabase(context).habitDao())
    }
    override val habitCompletionRepository: HabitCompletionRepository by lazy {
        OfflineHabitCompletionRepository(HabitTrackerDatabase.getHabitTrackerDatabase(context).habitCompletionDao())
    }

    override val combinedHabitRepository: CombinedHabitRepository by lazy {
        CombinedHabitRepositoryImpl(
            habitDao = HabitTrackerDatabase.getHabitTrackerDatabase(context).habitDao(),
            habitCompletionDao = HabitTrackerDatabase.getHabitTrackerDatabase(context).habitCompletionDao(),
            firestoreService = FirestoreService()
        )
    }

    override val themeRepository: ThemeRepository by lazy {
        ThemeRepository(context)
    }
}