package com.example.playlistmaker.ui.media.viewmodel

import com.example.playlistmaker.domain.search.model.Track

sealed class FavoritesState {
    object Empty : FavoritesState()
    data class Content(val tracks: List<Track>) : FavoritesState()
}