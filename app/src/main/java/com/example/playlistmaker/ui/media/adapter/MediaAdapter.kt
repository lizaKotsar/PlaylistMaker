package com.example.playlistmaker.ui.media.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.ui.media.fragment.FavoritesFragment
import com.example.playlistmaker.ui.media.fragment.PlaylistsFragment
//20
class MediaAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> FavoritesFragment.newInstance()
        else -> PlaylistsFragment.newInstance()
    }
}