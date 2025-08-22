package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {
    override fun addTrack(track: Track) = repository.addTrack(track)
    override fun getHistory(): List<Track> = repository.getHistory()
    override fun clearHistory() = repository.clearHistory()
}