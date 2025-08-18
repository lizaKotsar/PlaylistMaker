package com.example.playlistmaker.domain.api

interface PlayerInteractor {
    fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    )

    fun play()
    fun pause()
    fun toggle()
    fun isPlaying(): Boolean
    fun getPositionMs(): Int
    fun release()
}