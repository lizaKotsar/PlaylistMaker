package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.local.SearchHistory
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track

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