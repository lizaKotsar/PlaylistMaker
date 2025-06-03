package com.example.playlistmaker

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)


        val track = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("track")
        }


        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        if (track != null) {

            val coverImage = findViewById<ImageView>(R.id.coverImage)
            val artworkUrl512 = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
            Glide.with(this)
                .load(artworkUrl512)
                .placeholder(R.drawable.ic_placeholder)
                .into(coverImage)


            val maxTrackNameLength = 40
            val maxArtistNameLength = 40

            val trackTitle = track.trackName?.let {
                if (it.length > maxTrackNameLength) it.take(maxTrackNameLength) + "…" else it
            } ?: ""

            val artistTitle = track.artistName?.let {
                if (it.length > maxArtistNameLength) it.take(maxArtistNameLength) + "…" else it
            } ?: ""

            findViewById<TextView>(R.id.trackName).text = trackTitle
            findViewById<TextView>(R.id.artistName).text = artistTitle


            val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
            val duration = track.trackTimeMillis ?: 0L
            val formattedTime = formatter.format(duration)
            findViewById<TextView>(R.id.playbackTimer).text = formattedTime
            findViewById<TextView>(R.id.trackDurationValue).text = formattedTime


            val maxAlbumLength = 30
            val albumName = track.collectionName?.let {
                if (it.length > maxAlbumLength) it.take(maxAlbumLength) + "…" else it
            } ?: ""
            findViewById<TextView>(R.id.trackAlbumValue).text = albumName

            val year = track.releaseDate?.take(4).orEmpty()
            findViewById<TextView>(R.id.trackYearValue).text = year


            findViewById<TextView>(R.id.trackGenreValue).text = track.primaryGenreName.orEmpty()


            findViewById<TextView>(R.id.trackCountryValue).text = track.country.orEmpty()
        }
    }
}