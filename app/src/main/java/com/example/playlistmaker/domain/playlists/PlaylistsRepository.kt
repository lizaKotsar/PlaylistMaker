package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist

interface PlaylistsRepository {
    suspend fun createPlaylist(name: String, description: String?, coverPath: String?): Long
    suspend fun getPlaylists(): List<Playlist>
}