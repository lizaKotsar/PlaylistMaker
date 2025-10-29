package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmaker.data.bd.AppDatabase
import com.example.playlistmaker.data.bd.converters.FavoriteDbConverter
import com.example.playlistmaker.data.bd.dao.FavoriteTracksDao
import com.example.playlistmaker.data.bd.dao.PlaylistsDao
import com.example.playlistmaker.data.bd.dao.TracksInPlaylistsDao
import com.example.playlistmaker.data.search.local.SearchHistory
import com.example.playlistmaker.data.search.network.ITunesApi
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    factory { Gson() }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    single { SearchHistory(get(), get()) }

    single<NetworkClient> { RetrofitNetworkClient(get()) }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "playlist_bd"
        )
            .fallbackToDestructiveMigration()
            .build()
    }


    single<FavoriteTracksDao> { get<AppDatabase>().favoriteTracksDao() }
    single<PlaylistsDao> { get<AppDatabase>().playlistsDao() }
    single<TracksInPlaylistsDao> { get<AppDatabase>().tracksInPlaylistsDao() }

    factory { FavoriteDbConverter() }
}