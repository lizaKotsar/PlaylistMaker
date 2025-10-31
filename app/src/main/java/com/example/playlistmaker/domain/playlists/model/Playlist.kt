package com.example.playlistmaker.domain.playlists.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIds: List<Long> = emptyList(),
    val tracksCount: Int = 0
)