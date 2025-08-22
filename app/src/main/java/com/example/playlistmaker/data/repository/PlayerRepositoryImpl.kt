package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private var player: MediaPlayer? = null

    override fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        release()

        val mp = MediaPlayer()
        player = mp
        try {
            mp.setDataSource(url)
            mp.setOnPreparedListener { onPrepared() }
            mp.setOnCompletionListener { onCompletion() }
            mp.prepareAsync()
        } catch (e: Exception) {

            onCompletion()
        }
    }

    override fun play() { player?.start() }
    override fun pause() { player?.pause() }
    override fun isPlaying(): Boolean = player?.isPlaying == true
    override fun currentPosition(): Int = player?.currentPosition ?: 0

    override fun release() {
        player?.release()
        player = null
    }
}