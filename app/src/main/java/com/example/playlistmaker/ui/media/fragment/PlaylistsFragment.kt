package com.example.playlistmaker.ui.media.fragment



import android.graphics.Rect
import android.os.Bundle
import android.view.View
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
            findNavController().navigate(R.id.action_media_to_createPlaylist)
        }

        adapter = PlaylistsAdapter()
        rv.layoutManager = GridLayoutManager(requireContext(), 2)
        rv.adapter = adapter
        rv.addItemDecoration(SpacingDecoration(h = dp(16), v = dp(16)))

        vm.playlists.observe(viewLifecycleOwner) { list ->
            val isEmpty = list.isNullOrEmpty()
            rv.visibility = if (isEmpty) View.GONE else View.VISIBLE
            emptyIcon.visibility = if (isEmpty) View.VISIBLE else View.GONE
            emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
            if (!isEmpty) adapter.submit(list)
        }
    }

    override fun onResume() {
        super.onResume()
        vm.load()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private class SpacingDecoration(private val h: Int, private val v: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = h / 2
            outRect.right = h / 2
            outRect.top = v / 2
            outRect.bottom = v / 2
        }
    }
}