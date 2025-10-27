package com.example.playlistmaker.ui.media.fragment



import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.google.android.material.button.MaterialButton

class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().findViewById<View>(R.id.btn_new_playlist).setOnClickListener {
            findNavController().navigate(R.id.action_media_to_createPlaylist)
        }
    }
}