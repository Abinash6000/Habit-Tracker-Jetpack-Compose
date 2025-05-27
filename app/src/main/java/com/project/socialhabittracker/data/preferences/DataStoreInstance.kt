package com.project.socialhabittracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.socialhabittracker.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


object DataStoreInstance {
    val APP_THEME = stringPreferencesKey("AppTheme")

    private val Context.dataStore: DataStore<Preferences>
            by preferencesDataStore(name = "settings")

    suspend fun saveTheme(context: Context, key: Preferences.Key<String>, theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[key] = theme.name
        }
    }

    fun getTheme(context: Context, key: Preferences.Key<String>): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: AppTheme.Blue.name
        }
    }
}