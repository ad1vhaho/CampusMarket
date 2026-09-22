package com.example.campusmarket.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Stores CampusMarket application preferences using DataStore.
 */
private val Context.settingsDataStore by preferencesDataStore(
    name = "campus_market_settings"
)

/**
 * Provides access to saved application settings.
 */
class SettingsDataStore(
    private val context: Context
) {

    private companion object {

        val NOTIFICATIONS_ENABLED =
            booleanPreferencesKey(
                "notifications_enabled"
            )

        val DARK_MODE_ENABLED =
            booleanPreferencesKey(
                "dark_mode_enabled"
            )
    }

    /**
     * Returns the saved notification preference.
     *
     * Notifications are enabled by default.
     */
    val notificationsEnabled: Flow<Boolean> =
        context.settingsDataStore.data.map { preferences ->
            preferences[
                NOTIFICATIONS_ENABLED
            ] ?: true
        }

    /**
     * Returns the saved dark mode preference.
     *
     * Dark mode is disabled by default.
     */
    val darkModeEnabled: Flow<Boolean> =
        context.settingsDataStore.data.map { preferences ->
            preferences[
                DARK_MODE_ENABLED
            ] ?: false
        }

    /**
     * Saves the notification preference.
     */
    suspend fun setNotificationsEnabled(
        enabled: Boolean
    ) {

        context.settingsDataStore.edit { preferences ->

            preferences[
                NOTIFICATIONS_ENABLED
            ] = enabled
        }
    }

    /**
     * Saves the dark mode preference.
     */
    suspend fun setDarkModeEnabled(
        enabled: Boolean
    ) {

        context.settingsDataStore.edit { preferences ->

            preferences[
                DARK_MODE_ENABLED
            ] = enabled
        }
    }
}