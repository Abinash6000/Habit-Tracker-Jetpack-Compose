package com.project.socialhabittracker.data.preferences

import android.content.Context
import com.project.socialhabittracker.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow

class ThemeRepository(private val context: Context) {
    fun getTheme(): Flow<String> {
        return DataStoreInstance.getTheme(context, DataStoreInstance.APP_THEME)
    }

    suspend fun saveTheme(theme: AppTheme) {
        DataStoreInstance.saveTheme(context, DataStoreInstance.APP_THEME, theme)
    }
}