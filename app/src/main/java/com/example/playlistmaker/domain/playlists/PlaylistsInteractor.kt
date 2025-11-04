package com.example.playlistmaker.domain.playlists


import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track

interface PlaylistsInteractor {
    suspend fun create(name: String, description: String?, coverPath: String?): Long
    suspend fun getAll(): List<Playlist>
    suspend fun getPlaylist(id: Long): Playlist?
    suspend fun getTracksForPlaylist(playlistId: Long): List<Track>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Long): Boolean
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun update(playlist: Playlist)
}
