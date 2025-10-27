package com.example.playlistmaker.data.playlists.impl

import com.example.playlistmaker.data.bd.dao.PlaylistsDao
import com.example.playlistmaker.data.bd.entity.PlaylistEntity
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.google.gson.Gson

class PlaylistsRepositoryImpl(
    private val dao: PlaylistsDao,
    private val gson: Gson
) : PlaylistsRepository {

    override suspend fun createPlaylist(name: String, description: String?, coverPath: String?): Long {
        val entity = PlaylistEntity(
            name = name,
            description = description,
            coverPath = coverPath,
            trackIdsJson = gson.toJson(emptyList<Long>()),
            tracksCount = 0)
        return dao.insert(entity)
    }

    override suspend fun getPlaylists(): List<Playlist> =
        dao.getAll().map { it.toDomain(gson) }

    private fun PlaylistEntity.toDomain(gson: Gson) = Playlist(
        id = id,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIds = gson.fromJson(trackIdsJson, Array<Long>::class.java)?.toList() ?: emptyList(),
        tracksCount = tracksCount
    )
}