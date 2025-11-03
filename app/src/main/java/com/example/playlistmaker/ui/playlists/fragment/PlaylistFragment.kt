package com.example.playlistmaker.ui.playlists.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.playlists.adapter.PlaylistTracksAdapter
import com.example.playlistmaker.ui.playlists.viewmodel.PlaylistViewModel
import com.example.playlistmaker.ui.playlists.viewmodel.PlaylistViewModel.State
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistFragment : Fragment(R.layout.fragment_playlisttt) {

    private val vm: PlaylistViewModel by viewModel()
    private val args: PlaylistFragmentArgs by navArgs()

    private lateinit var ivCover: ImageView
    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvMeta: TextView
    private lateinit var rvTracks: RecyclerView

    private lateinit var tracksAdapter: PlaylistTracksAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivCover = view.findViewById(R.id.ivCover)
        btnBack = view.findViewById(R.id.btnBack)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvMeta = view.findViewById(R.id.tvMeta)
        rvTracks = view.findViewById(R.id.rvTracks)

        btnBack.setOnClickListener { findNavController().navigateUp() }

        // BottomSheet: не скрываемый
        val sheet: View = view.findViewById(R.id.sheet)
        sheetBehavior = BottomSheetBehavior.from(sheet).apply {
            isHideable = false
            // peekHeight задан в XML, можно оставить так
        }

        tracksAdapter = PlaylistTracksAdapter(
            onClick = { track -> navigateToPlayer(track) },
            onLongClick = { track -> confirmDelete(track) }
        )
        rvTracks.layoutManager = LinearLayoutManager(requireContext())
        rvTracks.adapter = tracksAdapter

        vm.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Loading -> Unit
                is State.Content -> {
                    val pl = state.playlist
                    val tracks = state.tracks

                    tvTitle.text = pl.name

                    val desc = pl.description?.trim().orEmpty()
                    if (desc.isNotEmpty()) {
                        tvDescription.text = desc
                        tvDescription.visibility = View.VISIBLE
                    } else {
                        tvDescription.visibility = View.GONE
                    }

                    Glide.with(ivCover)
                        .load(pl.coverPath)
                        .placeholder(R.drawable.cover_placeholder)
                        .centerCrop()
                        .into(ivCover)

                    tracksAdapter.submitList(tracks)
                    tvMeta.text = getString(
                        R.string.minutes_and_tracks_mask,
                        state.totalMinutes,
                        tracks.size
                    )
                }
                is State.Empty -> {
                    tvTitle.text = ""
                    tvDescription.visibility = View.GONE
                    tvMeta.text = ""
                    tracksAdapter.submitList(emptyList())
                }
                is State.Error -> {
                    tvMeta.text = state.message
                }
            }
        }

        vm.load(args.playlistId)
    }

    private fun navigateToPlayer(track: Track) {
        val action = PlaylistFragmentDirections.actionPlaylistToPlayer(track)
        findNavController().navigate(action)
    }

    private fun confirmDelete(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.delete_track_question)
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.yes) { dialog, _ ->
                vm.removeTrack(track)
                dialog.dismiss()
            }
            .show()
    }
}