package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.local.SearchHistory
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val storage: SearchHistory
) : SearchHistoryRepository {

    override fun getHistory(): List<Track> = storage.getHistory()

    override fun addTrack(track: Track) {
        storage.addTrack(track)
    }

    override fun clearHistory() {
        storage.clearHistory()
    }
}