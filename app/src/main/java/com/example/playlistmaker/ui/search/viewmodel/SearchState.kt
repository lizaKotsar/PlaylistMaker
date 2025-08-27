package com.example.playlistmaker.ui.search.viewmodel

import com.example.playlistmaker.domain.search.model.Track

sealed class SearchState {
    data object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class Empty(val message: String) : SearchState()
    data class Error(val message: String) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
}