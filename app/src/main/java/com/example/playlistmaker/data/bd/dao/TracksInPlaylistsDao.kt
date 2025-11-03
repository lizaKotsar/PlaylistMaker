package com.example.playlistmaker.data.bd.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.bd.entity.TrackInPlaylistEntity

@Dao
interface TracksInPlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: TrackInPlaylistEntity): Long

    @Query("SELECT * FROM tracks_in_playlists")
    suspend fun getAll(): List<TrackInPlaylistEntity>

    @Query("DELETE FROM tracks_in_playlists WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun delete(playlistId: Long, trackId: Long): Int
}