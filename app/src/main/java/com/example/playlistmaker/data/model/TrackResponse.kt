package com.example.playlistmaker.data.model
import com.example.playlistmaker.data.model.Track

data class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
)