package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response.resultCode == 200) {
            val body = response as TrackResponse
            body.results.map { it.toDomain() }
        } else {
            emptyList()
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