package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer)

    fun interface Consumer {
        fun consume(tracks: List<Track>)
    }
}