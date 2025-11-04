package com.example.playlistmaker.domain.playlists


import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track

interface PlaylistsRepository {
    suspend fun createPlaylist(name: String, description: String?, coverPath: String?): Long
    suspend fun getPlaylists(): List<Playlist>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun getTracksByIds(ids: List<Long>): List<Track>
    suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Long): Boolean
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun updatePlaylist(playlist: Playlist)
}