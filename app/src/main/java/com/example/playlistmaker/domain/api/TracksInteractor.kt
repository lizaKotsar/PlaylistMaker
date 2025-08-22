package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer)

    fun interface Consumer {
        fun consume(tracks: List<Track>)
    }
}