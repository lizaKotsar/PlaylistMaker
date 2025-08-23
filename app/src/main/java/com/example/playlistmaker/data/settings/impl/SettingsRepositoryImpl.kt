package com.example.playlistmaker.data.settings.impl

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.domain.settings.SettingsRepository

class SettingsRepositoryImpl(
    context: Context
) : SettingsRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("playlist_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DARK_THEME = "key_dark_theme"
    }

    override fun isDarkThemeEnabled(): Boolean =
        prefs.getBoolean(KEY_DARK_THEME, false)

    override fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }
}