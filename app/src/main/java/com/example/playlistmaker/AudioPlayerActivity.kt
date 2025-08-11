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

    private lateinit var playButton: ImageView
    private lateinit var playbackTimer: TextView
    private lateinit var durationText: TextView

    private val mediaPlayer = MediaPlayer()
    private lateinit var handler: android.os.Handler

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
        handler = android.os.Handler(mainLooper)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }

        // UI
        val coverImage = findViewById<ImageView>(R.id.coverImage)
        val trackNameTv = findViewById<TextView>(R.id.trackName)
        val artistNameTv = findViewById<TextView>(R.id.artistName)
        playbackTimer = findViewById(R.id.playbackTimer)
        durationText = findViewById(R.id.trackDurationValue)
        val albumTv = findViewById<TextView>(R.id.trackAlbumValue)
        val yearTv = findViewById<TextView>(R.id.trackYearValue)
        val genreTv = findViewById<TextView>(R.id.trackGenreValue)
        val countryTv = findViewById<TextView>(R.id.trackCountryValue)
        playButton = findViewById(R.id.playButton)

        track = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Track>("track")!!
        }


        val artworkUrl512 = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder)
            .into(coverImage)


        val trackTitle = track.trackName?.let { if (it.length > 40) it.take(40) + "…" else it }.orEmpty()
        val artistTitle = track.artistName?.let { if (it.length > 40) it.take(40) + "…" else it }.orEmpty()
        trackNameTv.text = trackTitle
        artistNameTv.text = artistTitle

        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        durationText.text = formatter.format(track.trackTimeMillis ?: 0L)
        playbackTimer.text = "00:00"

        val albumName = track.collectionName?.let { if (it.length > 30) it.take(30) + "…" else it }.orEmpty()
        albumTv.text = albumName
        yearTv.text = (track.releaseDate?.take(4)).orEmpty()
        genreTv.text = track.primaryGenreName.orEmpty()
        countryTv.text = track.country.orEmpty()


        android.util.Log.d("AudioPlayerActivity", "previewUrl = ${track.previewUrl}")
        preparePlayer(track.previewUrl)


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

            playButton.setImageResource(R.drawable.ic_play)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        startProgressUpdates()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play)
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