package com.example.playlistmaker.domain.playlists.impl

import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track

class PlaylistsInteractorImpl(
private val repo: PlaylistsRepository
) : PlaylistsInteractor {
    override suspend fun create(name: String, description: String?, coverPath: String?) =
        repo.createPlaylist(name, description, coverPath)

    override suspend fun getAll(): List<Playlist> = repo.getPlaylists()

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean =
        repo.addTrackToPlaylist(track, playlist)
}