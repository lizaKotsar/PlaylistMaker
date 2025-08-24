package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.impl.SharingRepositoryImpl
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.player.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.local.SearchHistory


import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl


import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl




import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl


import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.search.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.player.viewmodel.PlayerViewModel

object Creator {


    private fun provideSharingRepository(
        context: Context
    ): com.example.playlistmaker.domain.sharing.model.SharingRepository =
        SharingRepositoryImpl(context)

    private fun provideSharingInteractor(context: Context): SharingInteractor =
        SharingInteractorImpl(repository = provideSharingRepository(context))


    private fun provideSettingsRepository(
        context: Context
    ): com.example.playlistmaker.domain.settings.model.SettingsRepository =
        SettingsRepositoryImpl(context)

    private fun provideSettingsInteractor(context: Context): SettingsInteractor =
        SettingsInteractorImpl(repository = provideSettingsRepository(context))

    fun provideSettingsViewModelFactory(context: Context): ViewModelProvider.Factory {
        val sharing = provideSharingInteractor(context)
        val settings = provideSettingsInteractor(context)
        return SettingsVMFactory(sharing, settings)
    }

    private class SettingsVMFactory(
        private val sharingInteractor: SharingInteractor,
        private val settingsInteractor: SettingsInteractor,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(sharingInteractor, settingsInteractor) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }



    private fun provideNetworkClient() = RetrofitNetworkClient() // без Context

    private fun provideTracksRepository(): TracksRepository =
        TracksRepositoryImpl(provideNetworkClient())


    private fun provideSearchHistoryStorage(context: Context): SearchHistory =
        SearchHistory(context.getSharedPreferences("search_history", Context.MODE_PRIVATE))

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(storage = provideSearchHistoryStorage(context))

    private fun provideTracksInteractor(): TracksInteractor =
        TracksInteractorImpl(provideTracksRepository())

    private fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(provideSearchHistoryRepository(context))

    fun provideSearchViewModelFactory(context: Context): ViewModelProvider.Factory {
        val tracks = provideTracksInteractor()
        val history = provideSearchHistoryInteractor(context)
        return SearchVMFactory(tracks, history)
    }

    private class SearchVMFactory(
        private val tracksInteractor: TracksInteractor,
        private val historyInteractor: SearchHistoryInteractor,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(tracksInteractor, historyInteractor) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }


    private fun providePlayerRepository(): PlayerRepository = PlayerRepositoryImpl()

    private fun providePlayerInteractor(): PlayerInteractor =
        PlayerInteractorImpl(providePlayerRepository())

    fun providePlayerViewModelFactory(@Suppress("UNUSED_PARAMETER") context: Context): ViewModelProvider.Factory {
        val interactor = providePlayerInteractor()
        return PlayerVMFactory(interactor)
    }

    private class PlayerVMFactory(
        private val interactor: PlayerInteractor
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                return PlayerViewModel(interactor) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
