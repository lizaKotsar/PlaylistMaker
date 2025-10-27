package com.example.playlistmaker.data.bd.dao

import androidx.room.*
import com.example.playlistmaker.data.bd.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: FavoriteTrackEntity)
    @Delete
    suspend fun delete(track: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY added_at DESC")
    fun observeAll(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT track_id FROM favorite_tracks")
    suspend fun getAllIds(): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE track_id = :id)")
    suspend fun isFavorite(id: Long): Boolean
}