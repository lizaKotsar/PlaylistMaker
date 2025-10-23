package com.example.playlistmaker.domain.favorites

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getFavorites(): Flow<List<Track>>
    suspend fun isFavorite(trackId: Long): Boolean
}