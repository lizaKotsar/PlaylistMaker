package com.example.playlistmaker.data.player.impl

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private var player: MediaPlayer? = null

    override fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        release()

        val mp = MediaPlayer().apply {

            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onCompletion() }
            setOnErrorListener { _, _, _ ->

                release()
                onCompletion()
                true
            }
        }

        player = mp
        try {
            mp.setDataSource(url)
            mp.prepareAsync()
        } catch (e: Exception) {

            release()
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