package com.example.playlistmaker.creator


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R

// SEARCH
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.data.search.local.SearchHistory
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl

// SETTINGS
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl

// PLAYER
import com.example.playlistmaker.data.player.impl.PlayerRepositoryImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl

// SHARING

import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl

// VIEWMODELS
import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.search.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.player.viewmodel.PlayerViewModel

object Creator {

    // -------- SEARCH --------
    private fun networkClient(): NetworkClient = RetrofitNetworkClient()

    private fun tracksRepository(): TracksRepository =
        TracksRepositoryImpl(networkClient())

    fun provideTracksInteractor(): TracksInteractor =
        TracksInteractorImpl(tracksRepository())

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val prefs = context.getSharedPreferences("playlist_preferences", Context.MODE_PRIVATE)
        val storage = SearchHistory(prefs)
        val repo = SearchHistoryRepositoryImpl(storage)
        return SearchHistoryInteractorImpl(repo)
    }

    // -------- SETTINGS --------
    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val prefs = context.getSharedPreferences("playlist_preferences", Context.MODE_PRIVATE)
        val repo: SettingsRepository = SettingsRepositoryImpl(prefs)
        return SettingsInteractorImpl(repo)
    }

    // -------- PLAYER --------
    fun providePlayerInteractor(): PlayerInteractor {
        val repo = PlayerRepositoryImpl()
        return PlayerInteractorImpl(repo)
    }

    // -------- SHARING --------
    fun provideSharingInteractor(context: Context): SharingInteractor {
        val navigator = SharingInteractorImpl(context)

        val shareLink   = "https://practicum.yandex.ru/"
        val termsLink   = "https://yandex.ru/legal/practicum_termsofuse/"
        val supportEmail   = context.getString(R.string.support_email)
        val supportSubject = context.getString(R.string.support_tema)
        val supportBody    = context.getString(R.string.support_message)

        return com.example.playlistmaker.data.sharing.impl.SharingInteractorImpl(
            externalNavigator = navigator,
            shareAppLink = shareLink,
            termsLink = termsLink,
            supportEmail = supportEmail,
            supportSubject = supportSubject,
            supportBody = supportBody
        )
    }

    // -------- ViewModel factories --------
    fun provideSettingsViewModelFactory(context: Context): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                SettingsViewModel(
                    sharingInteractor  = provideSharingInteractor(context),
                    settingsInteractor = provideSettingsInteractor(context),
                )
            }
        }

    fun provideSearchViewModelFactory(context: Context): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                SearchViewModel(
                    tracksInteractor  = provideTracksInteractor(),
                    historyInteractor = provideSearchHistoryInteractor(context)
                )
            }
        }

    fun providePlayerViewModelFactory(): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlayerViewModel(providePlayerInteractor()) as T
            }
        }
}