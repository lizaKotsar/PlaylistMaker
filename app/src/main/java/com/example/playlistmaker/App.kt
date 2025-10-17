package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.viewModelModule

const val SETTINGS_PREFERENCES = "settings_preferences"
const val IS_DARK_THEME_ENABLED = "is_dark_theme_enabled"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
            //

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                viewModelModule
            )
        }


        val sharedPrefs = getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(IS_DARK_THEME_ENABLED, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val sharedPrefs = getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(IS_DARK_THEME_ENABLED, darkThemeEnabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}