package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    single<NetworkClient> { RetrofitNetworkClient(get()) }


    factory { Gson() }
    factory { FavoriteDbConverter() }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }
    single { SearchHistory(get(), get()) }


    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "playlist_bd"
        )

            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()
    }

    single<FavoriteTracksDao> { get<AppDatabase>().favoriteTracksDao() }
    single<PlaylistsDao> { get<AppDatabase>().playlistsDao() }
    single<TracksInPlaylistsDao> { get<AppDatabase>().tracksInPlaylistsDao() }
}


private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS tracks_in_playlists (
                trackId INTEGER NOT NULL PRIMARY KEY,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                trackTimeMillis INTEGER NOT NULL,
                artworkUrl100 TEXT,
                collectionName TEXT,
                releaseDate TEXT,
                primaryGenreName TEXT,
                country TEXT,
                previewUrl TEXT
            )
            """.trimIndent()
        )
    }
}