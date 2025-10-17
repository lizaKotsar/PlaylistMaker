package com.example.playlistmaker.ui.player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.viewmodel.PlayerViewModel
import com.example.playlistmaker.ui.player.viewmodel.TimeFormats
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel()

    companion object {
        private const val ARG_TRACK = "track"

        fun newInstance(track: Track) = AudioPlayerFragment().apply {
            arguments = bundleOf(ARG_TRACK to track)
        }
    }

    private lateinit var playButton: ImageButton
    private lateinit var playbackTimer: TextView
    private lateinit var durationText: TextView
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        track = requireArguments().getParcelable<Track>(ARG_TRACK)
            ?: error("Track argument is required")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_audio_player, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {

            activity?.onBackPressedDispatcher?.onBackPressed()

        }

        val coverImage = view.findViewById<ImageView>(R.id.coverImage)
        val trackNameTv = view.findViewById<TextView>(R.id.trackName)
        val artistNameTv = view.findViewById<TextView>(R.id.artistName)
        playbackTimer = view.findViewById(R.id.playbackTimer)
        durationText = view.findViewById(R.id.trackDurationValue)
        val albumTv = view.findViewById<TextView>(R.id.trackAlbumValue)
        val yearTv = view.findViewById<TextView>(R.id.trackYearValue)
        val genreTv = view.findViewById<TextView>(R.id.trackGenreValue)
        val countryTv = view.findViewById<TextView>(R.id.trackCountryValue)
        playButton = view.findViewById(R.id.playButton)

        val artworkUrl512 = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(view).load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder)
            .into(coverImage)

        fun limit(text: String?, max: Int) = text.orEmpty().let { if (it.length > max) it.take(max) + "…" else it }

        trackNameTv.text = limit(track.trackName, 40)
        artistNameTv.text = limit(track.artistName, 40)
        durationText.text = TimeFormats.mmss(track.trackTimeMillis ?: 0L)
        playbackTimer.text = getString(R.string.time)
        albumTv.text = limit(track.collectionName, 30)
        yearTv.text = (track.releaseDate?.take(4)).orEmpty()
        genreTv.text = track.primaryGenreName.orEmpty()
        countryTv.text = track.country.orEmpty()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            playButton.isEnabled = state.isPlayEnabled
            playbackTimer.text = state.timerText
            playButton.setImageResource(if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        }

        playButton.setOnClickListener { viewModel.onPlayPauseClicked() }

        viewModel.prepare(track.previewUrl)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}