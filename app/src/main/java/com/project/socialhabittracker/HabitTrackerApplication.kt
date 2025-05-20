package com.project.socialhabittracker

import android.app.Application
import com.project.socialhabittracker.data.AppContainer
import com.project.socialhabittracker.data.AppDataContainer

class HabitTrackerApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}