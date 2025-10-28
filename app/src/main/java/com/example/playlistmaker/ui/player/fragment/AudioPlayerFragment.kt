package com.example.playlistmaker.ui.player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.adapter.BottomSheetPlaylistsAdapter
import com.example.playlistmaker.ui.player.viewmodel.PlayerViewModel
import com.example.playlistmaker.ui.player.viewmodel.TimeFormats
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {

    private val viewModel: PlayerViewModel by viewModel()

    companion object {
        private const val ARG_TRACK = "track"
        fun newInstance(track: Track) = AudioPlayerFragment().apply {
            arguments = bundleOf(ARG_TRACK to track)
        }
    }


    private lateinit var playButton: ImageButton
    private lateinit var favoriteButton: ImageButton
    private lateinit var playbackTimer: TextView
    private lateinit var durationText: TextView
    private lateinit var track: Track


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<out View>
    private lateinit var overlay: View
    private lateinit var rvPlaylists: RecyclerView
    private lateinit var btnNewPlaylist: MaterialButton
    private val sheetAdapter = BottomSheetPlaylistsAdapter { playlist ->
        viewModel.addTrackToPlaylist(playlist)
    }

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
        favoriteButton = view.findViewById(R.id.button_favorite)

        val artworkUrl512 = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(view)
            .load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .into(coverImage)

        fun limit(text: String?, max: Int) =
            text.orEmpty().let { if (it.length > max) it.take(max) + "…" else it }

        trackNameTv.text = limit(track.trackName, 40)
        artistNameTv.text = limit(track.artistName, 40)
        durationText.text = TimeFormats.mmss(track.trackTimeMillis ?: 0L)
        playbackTimer.text = getString(R.string.time)
        albumTv.text = limit(track.collectionName, 30)
        yearTv.text = (track.releaseDate?.take(4)).orEmpty()
        genreTv.text = track.primaryGenreName.orEmpty()
        countryTv.text = track.country.orEmpty()


        viewModel.setTrack(track)

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            playButton.isEnabled = state.isPlayEnabled
            playbackTimer.text = state.timerText
            playButton.setImageResource(
                if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
            favoriteButton.setImageResource(
                if (state.isFavorite) R.drawable.ic_favorite_red else R.drawable.ic_favorite
            )
        }

        playButton.setOnClickListener { viewModel.onPlayPauseClicked() }
        favoriteButton.setOnClickListener { viewModel.onFavoriteClicked() }
        viewModel.prepare(track.previewUrl)


        overlay = view.findViewById(R.id.overlay)
        rvPlaylists = view.findViewById(R.id.rvPlaylists)
        btnNewPlaylist = view.findViewById(R.id.btnNewPlaylist)

        rvPlaylists.layoutManager = LinearLayoutManager(requireContext())
        rvPlaylists.adapter = sheetAdapter

        val sheet = view.findViewById<View>(R.id.playlists_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(sheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
                if (newState != BottomSheetBehavior.STATE_HIDDEN) {

                    viewModel.loadPlaylists()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                overlay.alpha = ((slideOffset + 1f) / 2f).coerceIn(0f, 1f)
            }
        })

        overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }


        view.findViewById<View>(R.id.button_add_to_playlist).setOnClickListener {
            viewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        btnNewPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            findNavController().navigate(R.id.action_audioPlayer_to_createPlaylist)
        }


        viewModel.bsPlaylists.observe(viewLifecycleOwner) { list ->
            sheetAdapter.submit(list)
        }
        viewModel.addResult.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}