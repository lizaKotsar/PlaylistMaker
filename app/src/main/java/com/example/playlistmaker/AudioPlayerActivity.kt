package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.data.model.Track
import java.text.SimpleDateFormat
import java.util.*

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    private lateinit var playButton: ImageView   // если у тебя Button — поменяй тип и setImageResource на setText
    private lateinit var playbackTimer: TextView
    private lateinit var durationText: TextView

    private val mediaPlayer = MediaPlayer()
    private val handler = android.os.Handler(mainLooper)
    private val progressRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                playbackTimer.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition)
                handler.postDelayed(this, 300L)
            }
        }
    }

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        // Навигация назад
        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }

        // UI
        val coverImage = findViewById<ImageView>(R.id.coverImage)
        val trackNameTv = findViewById<TextView>(R.id.trackName)
        val artistNameTv = findViewById<TextView>(R.id.artistName)
        playbackTimer = findViewById(R.id.playbackTimer)     // ← текущий прогресс (mm:ss)
        durationText = findViewById(R.id.trackDurationValue) // ← фиксированная длительность трека
        val albumTv = findViewById<TextView>(R.id.trackAlbumValue)
        val yearTv = findViewById<TextView>(R.id.trackYearValue)
        val genreTv = findViewById<TextView>(R.id.trackGenreValue)
        val countryTv = findViewById<TextView>(R.id.trackCountryValue)
        playButton = findViewById(R.id.playButton) // проверь id

        // Получаем трек из интента
        track = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Track>("track")!!
        }

        // Обложка
        val artworkUrl512 = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder)
            .into(coverImage)

        // Тексты (с усечением, как было у тебя)
        val trackTitle = track.trackName?.let { if (it.length > 40) it.take(40) + "…" else it }.orEmpty()
        val artistTitle = track.artistName?.let { if (it.length > 40) it.take(40) + "…" else it }.orEmpty()
        trackNameTv.text = trackTitle
        artistNameTv.text = artistTitle

        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        durationText.text = formatter.format(track.trackTimeMillis ?: 0L) // фиксированная длительность
        playbackTimer.text = "00:00" // прогресс всегда начинается с 00:00

        val albumName = track.collectionName?.let { if (it.length > 30) it.take(30) + "…" else it }.orEmpty()
        albumTv.text = albumName
        yearTv.text = (track.releaseDate?.take(4)).orEmpty()
        genreTv.text = track.primaryGenreName.orEmpty()
        countryTv.text = track.country.orEmpty()

        // Готовим плеер
        preparePlayer(track.previewUrl)

        // Кнопка Play/Pause
        playButton.setOnClickListener { playbackControl() }
    }

    private fun preparePlayer(url: String?) {
        playbackTimer.text = "00:00"
        playButton.isEnabled = false

        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            stopProgressUpdates()
            playbackTimer.text = "00:00"
            // если у тебя иконки — поставь play-иконку; если текст — playButton.text = "PLAY"
            playButton.setImageResource(R.drawable.ic_play)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause) // или playButton.text = "PAUSE"
        playerState = STATE_PLAYING
        startProgressUpdates()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play) // или playButton.text = "PLAY"
        playerState = STATE_PAUSED
        stopProgressUpdates()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startProgressUpdates() {
        handler.removeCallbacks(progressRunnable)
        handler.post(progressRunnable)
    }

    private fun stopProgressUpdates() {
        handler.removeCallbacks(progressRunnable)
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgressUpdates()
        mediaPlayer.release()
    }

    override fun onBackPressed() {
        if (playerState == STATE_PLAYING) pausePlayer()
        super.onBackPressed()
    }
}