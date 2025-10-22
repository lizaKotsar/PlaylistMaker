package com.example.playlistmaker.data.favorites.impl

import com.example.playlistmaker.data.bd.converters.FavoriteDbConverter
import com.example.playlistmaker.data.bd.dao.FavoriteTracksDao
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val dao: FavoriteTracksDao,
    private val converter: FavoriteDbConverter
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        dao.insert(converter.toEntity(track))
    }

    override suspend fun removeFromFavorites(track: Track) {
        dao.delete(converter.toEntity(track))
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        emit(dao.getAll())
    }.map { list -> list.map(converter::fromEntity) }
}