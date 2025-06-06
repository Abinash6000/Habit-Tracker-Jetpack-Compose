package com.project.socialhabittracker

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.project.socialhabittracker.ui.add_habit.AddHabitViewModel
import com.project.socialhabittracker.ui.edit_habit.EditHabitViewModel
import com.project.socialhabittracker.ui.habit_report.HabitReportViewModel
import com.project.socialhabittracker.ui.home.HomeViewModel
import com.project.socialhabittracker.ui.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            HomeViewModel(
                habitTrackerApplication().container.habitRepository,
                habitTrackerApplication().container.habitCompletionRepository,
                habitTrackerApplication().container.combinedHabitRepository
            )
        }

        initializer {
            AddHabitViewModel(
                habitTrackerApplication().container.habitRepository
            )
        }

        initializer {
            HabitReportViewModel(
                this.createSavedStateHandle(),
                habitTrackerApplication().container.habitRepository,
                habitTrackerApplication().container.habitCompletionRepository
            )
        }

        initializer {
            EditHabitViewModel(
                this.createSavedStateHandle(),
                habitTrackerApplication().container.habitRepository
            )
        }

//        initializer {
//            OverallReportViewModel(
//                habitTrackerApplication().container.habitRepository,
//                habitTrackerApplication().container.habitCompletionRepository
//            )
//        }

        initializer {
            SettingsViewModel(
                habitTrackerApplication().container.combinedHabitRepository
            )
        }

        initializer {
            MainViewModel(
                habitTrackerApplication().container.themeRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.habitTrackerApplication(): HabitTrackerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HabitTrackerApplication)