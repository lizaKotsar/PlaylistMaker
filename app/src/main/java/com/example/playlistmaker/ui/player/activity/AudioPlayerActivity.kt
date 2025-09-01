package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.fragment.AudioPlayerFragment

class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        if (savedInstanceState == null) {
            val track: Track = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("track", Track::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Track>("track")!!
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.player_container, AudioPlayerFragment.newInstance(track))
                .commit()
        }
    }
}