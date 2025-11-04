package com.example.playlistmaker.ui.playlists.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var btnShare: ImageButton
    private lateinit var btnMenu: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvMeta: TextView
    private lateinit var rvTracks: RecyclerView

    private lateinit var tracksAdapter: PlaylistTracksAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<View>

    // Меню
    private lateinit var scrim: View
    private lateinit var menuSheet: View
    private lateinit var menuBehavior: BottomSheetBehavior<View>
    private lateinit var menuShare: View
    private lateinit var menuEdit: View
    private lateinit var menuDelete: View
    private lateinit var menuCover: ImageView
    private lateinit var menuTitle: TextView
    private lateinit var menuCount: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivCover = view.findViewById(R.id.ivCover)
        btnBack = view.findViewById(R.id.btnBack)
        btnShare = view.findViewById(R.id.btnShare)
        btnMenu = view.findViewById(R.id.btnMenu)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvMeta = view.findViewById(R.id.tvMeta)
        rvTracks = view.findViewById(R.id.rvTracks)

        // Вторая шторка (меню)
        scrim = view.findViewById(R.id.scrim)
        menuSheet = view.findViewById(R.id.menuSheet)
        menuShare = view.findViewById(R.id.menuShare)
        menuEdit = view.findViewById(R.id.menuEdit)
        menuDelete = view.findViewById(R.id.menuDelete)
        menuCover = view.findViewById(R.id.menuCover)
        menuTitle = view.findViewById(R.id.menuTitle)
        menuCount = view.findViewById(R.id.menuCount)

        btnBack.setOnClickListener { findNavController().navigateUp() }

        // BottomSheet со списком треков
        val sheet: View = view.findViewById(R.id.sheet)
        sheetBehavior = BottomSheetBehavior.from(sheet).apply { isHideable = false }
        view.post { updatePeekForTracks() }

        // BottomSheet меню
        menuBehavior = BottomSheetBehavior.from(menuSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    scrim.visibility =
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        // вернём высоту по умолчанию, чтобы поведение не ломалось
                        val lp = menuSheet.layoutParams
                        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        menuSheet.layoutParams = lp
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    scrim.alpha = (0.0001f + (slideOffset.coerceIn(0f, 1f) * 0.6f))
                }
            })
        }
        scrim.setOnClickListener { menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN }

        btnMenu.setOnClickListener { openMenu() } // важный фикс
        btnShare.setOnClickListener { sharePlaylist() }
        menuShare.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }
        menuEdit.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val action = PlaylistFragmentDirections.actionPlaylistToEdit(args.playlistId)
            findNavController().navigate(action)
        }
        menuDelete.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            confirmDeletePlaylist()
        }

        tracksAdapter = PlaylistTracksAdapter(
            onClick = { track -> navigateToPlayer(track) },
            onLongClick = { track -> confirmDeleteTrack(track) }
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

                    // заголовок меню
                    menuTitle.text = pl.name
                    menuCount.text = "${tracks.size} треков"
                    Glide.with(menuCover)
                        .load(pl.coverPath)
                        .placeholder(R.drawable.ic_placeholder)
                        .centerCrop()
                        .into(menuCover)

                    tracksAdapter.submitList(tracks)
                    tvMeta.text = getString(
                        R.string.minutes_and_tracks_mask,
                        state.totalMinutes,
                        tracks.size
                    )

                    view.post { updatePeekForTracks() }
                }
                is State.Empty -> {
                    tvTitle.text = ""
                    tvDescription.visibility = View.GONE
                    tvMeta.text = ""
                    tracksAdapter.submitList(emptyList())
                    view.post { updatePeekForTracks() }
                }
                is State.Error -> {
                    tvMeta.text = state.message
                    view.post { updatePeekForTracks() }
                }
            }
        }

        vm.finish.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        vm.load(args.playlistId)
    }

    override fun onResume() {
        super.onResume()
        vm.load(args.playlistId)
        view?.post { updatePeekForTracks() }
    }

    /** Делает верх списка треков почти под кнопками Share/⋯ */
    private fun updatePeekForTracks() {
        val root = requireView().findViewById<View>(R.id.root)
        val header = requireView().findViewById<View>(R.id.header)
        val gap = resources.getDimensionPixelSize(R.dimen.playlist_sheet_gap)
        val available = root.height - (header.bottom + gap)
        if (available > 0) sheetBehavior.peekHeight = available
    }

    /** Открывает меню строго под заголовком, перекрывая список треков */
    private fun openMenu() {
        val root = requireView().findViewById<View>(R.id.root)
        val title = requireView().findViewById<View>(R.id.tvTitle)   // якоримся под названием
        val gap = resources.getDimensionPixelSize(R.dimen.playlist_sheet_gap) // 8dp (можно 0–4dp)

        val top = title.bottom + gap
        val desiredHeight = (root.height - top).coerceAtLeast(0)

        val lp = menuSheet.layoutParams
        lp.height = desiredHeight
        menuSheet.layoutParams = lp
        menuSheet.requestLayout()

        menuBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun navigateToPlayer(track: Track) {
        val action = PlaylistFragmentDirections.actionPlaylistToPlayer(track)
        findNavController().navigate(action)
    }

    private fun confirmDeleteTrack(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.delete_track_question)
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.yes) { dialog, _ ->
                vm.removeTrack(track)
                dialog.dismiss()
            }
            .show()
    }

    private fun sharePlaylist() {
        val text = vm.buildShareText()
        if (text.isNullOrBlank()) {
            Toast.makeText(requireContext(), R.string.playlist_share_empty, Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun confirmDeletePlaylist() {
        val name = (vm.state.value as? State.Content)?.playlist?.name.orEmpty()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_playlist_title)
            .setMessage(getString(R.string.delete_playlist_question, name))
            .setNegativeButton(R.string.no) { d, _ -> d.dismiss() }
            .setPositiveButton(R.string.yes) { d, _ ->
                vm.deletePlaylist()
                d.dismiss()
            }
            .show()
    }
}
