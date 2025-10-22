package com.example.playlistmaker.ui.media.fragment


import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.ui.media.viewmodel.FavoritesViewModel
import com.example.playlistmaker.ui.media.viewmodel.FavoritesState
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var recycler: RecyclerView
    private lateinit var emptyGroup: View
    private lateinit var adapter: TrackAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        emptyGroup = view.findViewById(R.id.emptyGroup)


        recycler = view.findViewById(R.id.rvFavorites)
        adapter = TrackAdapter(arrayListOf())
        recycler.adapter = adapter


        adapter.setOnItemClickListener { track ->
            findNavController().navigate(
                R.id.action_media_to_player,
                bundleOf("track" to track)
            )
        }


        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Empty -> {
                    emptyGroup.isVisible = true
                    recycler.isVisible = false
                }
                is FavoritesState.Content -> {
                    emptyGroup.isVisible = false
                    recycler.isVisible = true
                    adapter.updateTracks(state.tracks)
                }
            }
        }
    }
}