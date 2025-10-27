package com.example.playlistmaker.data.bd

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.bd.dao.FavoriteTracksDao
import com.example.playlistmaker.data.bd.dao.PlaylistsDao
import com.example.playlistmaker.data.bd.entity.FavoriteTrackEntity
import com.example.playlistmaker.data.bd.entity.PlaylistEntity


@Database(
    version = 2,
    entities = [
        FavoriteTrackEntity::class,
        PlaylistEntity::class
    ],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistsDao(): PlaylistsDao
}