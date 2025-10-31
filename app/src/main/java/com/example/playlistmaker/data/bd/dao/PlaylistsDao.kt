package com.example.playlistmaker.data.bd.dao

import androidx.room.*
import com.example.playlistmaker.data.bd.entity.PlaylistEntity

@Dao
interface PlaylistsDao {
    @Insert
    suspend fun insert(entity: PlaylistEntity): Long

    @Query("SELECT * FROM playlists ORDER BY id DESC")
    suspend fun getAll(): List<PlaylistEntity>

    @Update
    suspend fun update(entity: PlaylistEntity)
}