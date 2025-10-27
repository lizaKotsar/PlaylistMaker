package com.example.playlistmaker.di

import com.example.playlistmaker.data.player.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.data.sharing.impl.SharingRepositoryImpl
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.settings.model.SettingsRepository
import com.example.playlistmaker.domain.sharing.model.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.example.playlistmaker.data.favorites.impl.FavoritesRepositoryImpl
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.data.bd.dao.FavoriteTracksDao

val repositoryModule = module {


    single<TracksRepository> { TracksRepositoryImpl(get(), get<FavoriteTracksDao>()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }


    single<SettingsRepository> { SettingsRepositoryImpl(androidContext()) }


    single<PlayerRepository> { PlayerRepositoryImpl() }


    single<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }
    single<SharingRepository> { SharingRepositoryImpl(androidContext()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get(), get()) }
}