package com.example.playlistmaker.data.bd.converters

import com.example.playlistmaker.data.bd.entity.FavoriteTrackEntity
import com.example.playlistmaker.domain.search.model.Track

class FavoriteDbConverter {

    fun toEntity(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId = track.trackId ?: 0L,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl
        )
    }

    fun fromEntity(entity: FavoriteTrackEntity): Track {
        return Track(
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            trackId = entity.trackId,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }
}