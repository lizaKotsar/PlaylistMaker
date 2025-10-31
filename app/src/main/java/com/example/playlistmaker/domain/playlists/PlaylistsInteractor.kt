package com.example.playlistmaker.domain.playlists


import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track

interface PlaylistsInteractor {
    suspend fun create(name: String, description: String?, coverPath: String?): Long
    suspend fun getAll(): List<Playlist>


    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean
}