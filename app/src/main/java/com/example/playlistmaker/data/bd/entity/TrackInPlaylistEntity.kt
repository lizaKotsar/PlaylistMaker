package com.example.playlistmaker.data.bd.entity

import androidx.room.Entity


@Entity(
    tableName = "tracks_in_playlists",
    primaryKeys = ["playlistId", "trackId"]
)
data class TrackInPlaylistEntity(
    val playlistId: Long,
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
)