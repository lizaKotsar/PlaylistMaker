package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository

class PlayerInteractorImpl(
    private val repo: PlayerRepository
) : PlayerInteractor {

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) =
        repo.prepare(url, onPrepared, onCompletion)

    override fun play() = repo.play()
    override fun pause() = repo.pause()

    override fun toggle() {
        if (repo.isPlaying()) repo.pause() else repo.play()
    }

    override fun isPlaying(): Boolean = repo.isPlaying()
    override fun getPositionMs(): Int = repo.currentPosition()
    override fun release() = repo.release()
}
