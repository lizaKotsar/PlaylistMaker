package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.viewmodel.PlayerViewModel

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel

    private lateinit var playButton: ImageButton
    private lateinit var playbackTimer: TextView
    private lateinit var durationText: TextView

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }

        // UI refs
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

        // track from intent
        track = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Track>("track")!!
        }

        // static UI
        val artworkUrl512 = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this).load(artworkUrl512).placeholder(R.drawable.ic_placeholder).into(coverImage)

        trackNameTv.text = track.trackName.orEmpty().let { if (it.length > 40) it.take(40) + "…" else it }
        artistNameTv.text = track.artistName.orEmpty().let { if (it.length > 40) it.take(40) + "…" else it }
        durationText.text = com.example.playlistmaker.ui.player.viewmodel.TimeFormats.mmss(track.trackTimeMillis ?: 0L)
        playbackTimer.text = getString(R.string.time)
        albumTv.text = track.collectionName.orEmpty().let { if (it.length > 30) it.take(30) + "…" else it }
        yearTv.text = (track.releaseDate?.take(4)).orEmpty()
        genreTv.text = track.primaryGenreName.orEmpty()
        countryTv.text = track.country.orEmpty()

        // VM
        viewModel = ViewModelProvider(this, Creator.providePlayerViewModelFactory())
            .get(PlayerViewModel::class.java)

        // observe state
        viewModel.observeState().observe(this) { state ->
            playButton.isEnabled = state.isPlayEnabled
            playbackTimer.text = state.timerText
            playButton.setImageResource(if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        }

        // actions
        playButton.setOnClickListener { viewModel.onPlayPauseClicked() }

        // prepare
        viewModel.prepare(track.previewUrl)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}