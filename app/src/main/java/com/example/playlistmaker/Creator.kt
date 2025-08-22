package com.example.playlistmaker


import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import android.content.Context

import com.example.playlistmaker.data.local.SearchHistory
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl

import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl

object Creator {
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
    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val prefs = context.getSharedPreferences("playlist_preferences", Context.MODE_PRIVATE)
        val repo: SettingsRepository = SettingsRepositoryImpl(prefs)
        return SettingsInteractorImpl(repo)
    }
    fun providePlayerInteractor(): PlayerInteractor {
        val repo = PlayerRepositoryImpl()
        return PlayerInteractorImpl(repo)
    }

}