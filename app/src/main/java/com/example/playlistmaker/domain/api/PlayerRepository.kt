package com.example.playlistmaker.domain.api

interface PlayerRepository {
    fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    )

    fun play()
    fun pause()
    fun isPlaying(): Boolean
    fun currentPosition(): Int
    fun release()
}