package com.example.playlistmaker.data.settings.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.settings.model.SettingsRepository

class SettingsRepositoryImpl(
    context: Context
) : SettingsRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isDarkThemeEnabled(): Boolean =
        prefs.getBoolean(KEY_DARK_THEME, false)

    override fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_THEME, enabled) }
    }

    private companion object {
        const val PREFS_NAME = "playlist_settings"
        const val KEY_DARK_THEME = "dark_theme_enabled"
    }
}
