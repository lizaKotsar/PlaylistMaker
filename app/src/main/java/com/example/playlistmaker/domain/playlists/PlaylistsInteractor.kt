package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist

interface PlaylistsInteractor {
    suspend fun create(name: String, description: String?, coverPath: String?): Long
    suspend fun getAll(): List<Playlist>
}