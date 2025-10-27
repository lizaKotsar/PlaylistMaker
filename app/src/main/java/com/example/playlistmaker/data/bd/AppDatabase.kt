package com.example.playlistmaker.data.bd

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.bd.dao.FavoriteTracksDao
import com.example.playlistmaker.data.bd.entity.FavoriteTrackEntity

@Database(
    version = 1,
    entities = [FavoriteTrackEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
}