package com.example.playlistmaker.data.bd.dao

import androidx.room.*
import com.example.playlistmaker.data.bd.entity.FavoriteTrackEntity

@Dao
interface FavoriteTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: FavoriteTrackEntity): Long

    @Delete
    suspend fun delete(track: FavoriteTrackEntity): Int

    @Query("SELECT * FROM favorite_tracks ORDER BY added_at DESC")
    suspend fun getAll(): List<FavoriteTrackEntity>

    @Query("SELECT track_id FROM favorite_tracks")
    suspend fun getAllIds(): List<Long>
}