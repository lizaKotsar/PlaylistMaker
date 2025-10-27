package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.common.Resource
import com.example.playlistmaker.data.search.dto.TrackDto
import com.example.playlistmaker.data.search.dto.TrackResponse
import com.example.playlistmaker.data.search.dto.TracksSearchRequest
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.playlistmaker.data.bd.dao.FavoriteTracksDao

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favoritesDao: FavoriteTracksDao
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            200 -> {
                val body = response as TrackResponse
                val favoriteIds = favoritesDao.getAllIds().toSet()
                val data = body.results.map { it.toDomain().apply {
                    isFavorite = trackId != null && favoriteIds.contains(trackId)
                } }
                emit(Resource.Success(data))
            }
            else -> emit(Resource.Error("SERVER_ERROR"))
        }
    }

    private fun TrackDto.toDomain() = Track(
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        artworkUrl100 = artworkUrl100,
        trackId = trackId,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}