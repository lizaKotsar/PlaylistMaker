package com.example.playlistmaker.ui.media.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.media.adapter.PlaylistsAdapter
import com.example.playlistmaker.ui.media.viewmodel.PlaylistsViewModel
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {

    private val vm: PlaylistsViewModel by viewModel()

    private lateinit var rv: RecyclerView
    private lateinit var adapter: PlaylistsAdapter
    private lateinit var btnNew: MaterialButton
    private lateinit var emptyIcon: View
    private lateinit var emptyText: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnNew = view.findViewById(R.id.btn_new_playlist)
        rv = view.findViewById(R.id.rvPlaylists)
        emptyIcon = view.findViewById(R.id.iv_note)
        emptyText = view.findViewById(R.id.tv_empty)


        btnNew.setOnClickListener {
            findNavController().navigate(R.id.createPlaylistFragment)
        }

        adapter = PlaylistsAdapter { playlist ->
            findNavController().navigate(
                R.id.playlistFragment,
                bundleOf("playlistId" to playlist.id)
            )
        }

        rv.layoutManager = GridLayoutManager(requireContext(), 2)
        rv.adapter = adapter
        rv.setHasFixedSize(true)
        rv.addItemDecoration(SpacingDecoration(h = 16.dp, v = 16.dp))

        vm.playlists.observe(viewLifecycleOwner) { list ->
            val isEmpty = list.isNullOrEmpty()
            rv.isVisible = !isEmpty
            emptyIcon.isVisible = isEmpty
            emptyText.isVisible = isEmpty
            adapter.submitList(list ?: emptyList())
        }
    }

    override fun onResume() {
        super.onResume()
        vm.load()
    }


    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    private class SpacingDecoration(private val h: Int, private val v: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(h / 2, v / 2, h / 2, v / 2)
        }
    }
}