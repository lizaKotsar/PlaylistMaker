package com.example.playlistmaker.data.playlists.impl


import com.example.playlistmaker.data.bd.dao.PlaylistsDao
import com.example.playlistmaker.data.bd.dao.TracksInPlaylistsDao
import com.example.playlistmaker.data.bd.entity.PlaylistEntity
import com.example.playlistmaker.data.bd.entity.TrackInPlaylistEntity
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson

class PlaylistsRepositoryImpl(
    private val dao: PlaylistsDao,
    private val tracksDao: TracksInPlaylistsDao,
    private val gson: Gson
) : PlaylistsRepository {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long {
        val entity = PlaylistEntity(
            name = name,
            description = description,
            coverPath = coverPath,
            trackIdsJson = gson.toJson(emptyList<Long>()),
            tracksCount = 0
        )
        return dao.insert(entity)
    }

    override suspend fun getPlaylists(): List<Playlist> =
        dao.getAll().map { it.toDomain(gson) }

    override suspend fun getPlaylistById(id: Long): Playlist? =
        dao.getById(id)?.toDomain(gson)

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        val id = track.trackId ?: return false

        val rowId = tracksDao.insert(track.toTracksInPlaylistEntity(playlist.id))
        if (rowId == -1L) return false

        val newIds = playlist.trackIds.toMutableList().apply { add(0, id) }
        dao.update(
            PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                coverPath = playlist.coverPath,
                trackIdsJson = gson.toJson(newIds),
                tracksCount = newIds.size
            )
        )
        return true
    }

    override suspend fun getTracksByIds(ids: List<Long>): List<Track> {
        if (ids.isEmpty()) return emptyList()
        val all = tracksDao.getAll()
        val byId = all.associateBy { it.trackId }
        return ids.mapNotNull { byId[it] }.map { it.toDomain() }
    }

    override suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Long): Boolean {
        val affected = tracksDao.delete(playlist.id, trackId)
        if (affected <= 0) return false

        val newIds = playlist.trackIds.toMutableList().apply { remove(trackId) }
        dao.update(
            PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                coverPath = playlist.coverPath,
                trackIdsJson = gson.toJson(newIds),
                tracksCount = newIds.size
            )
        )
        return true
    }
    override suspend fun deletePlaylist(playlistId: Long) {
        tracksDao.deleteByPlaylist(playlistId)
        dao.deleteById(playlistId)

    }


    private fun PlaylistEntity.toDomain(gson: Gson) = Playlist(
        id = id,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIds = gson.fromJson(trackIdsJson, Array<Long>::class.java)?.toList() ?: emptyList(),
        tracksCount = tracksCount
    )

    private fun Track.toTracksInPlaylistEntity(playlistId: Long) = TrackInPlaylistEntity(
        playlistId = playlistId,
        trackId = trackId ?: 0L,
        trackName = trackName.orEmpty(),
        artistName = artistName.orEmpty(),
        trackTimeMillis = trackTimeMillis ?: 0L,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )

    private fun TrackInPlaylistEntity.toDomain() = Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}