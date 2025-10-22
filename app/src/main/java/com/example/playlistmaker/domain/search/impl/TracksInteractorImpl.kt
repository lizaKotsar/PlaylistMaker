package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.common.Resource
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> =
        repository.searchTracks(expression)
}