package com.example.playlistmaker.data.bd.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.data.bd.entity.TrackInPlaylistEntity

@Dao
interface TracksInPlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: TrackInPlaylistEntity)
}