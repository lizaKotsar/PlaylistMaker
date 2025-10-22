package com.example.playlistmaker.di

import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.dsl.module
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.favorites.impl.FavoritesInteractorImpl

val interactorModule = module {
    single<TracksInteractor> { TracksInteractorImpl(get()) }
    single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
    single<SettingsInteractor> { SettingsInteractorImpl(get()) }
    single<PlayerInteractor> { PlayerInteractorImpl(get()) }
    single<SharingInteractor> { SharingInteractorImpl(get()) }
    single<FavoritesInteractor> { FavoritesInteractorImpl(get()) }
}