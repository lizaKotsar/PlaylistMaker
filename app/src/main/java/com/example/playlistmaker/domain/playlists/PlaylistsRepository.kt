package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track

interface PlaylistsRepository {
    suspend fun createPlaylist(name: String, description: String?, coverPath: String?): Long
    suspend fun getPlaylists(): List<Playlist>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean

}