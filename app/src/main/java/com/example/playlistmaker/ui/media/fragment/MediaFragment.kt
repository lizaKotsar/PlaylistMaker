package com.example.playlistmaker.ui.media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.ui.media.adapter.MediaAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaFragment : Fragment() {

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    private var mediator: TabLayoutMediator? = null

    companion object {
        private const val KEY_SELECTED_PAGE = "media_selected_page"
    }


    private var selectedPage: Int = 0


    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            selectedPage = position
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedPage = savedInstanceState?.getInt(KEY_SELECTED_PAGE) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = MediaAdapter(
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )

        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> getString(R.string.favorites_tab)
                else -> getString(R.string.playlists_tab)
            }
        }.also { it.attach() }


        binding.viewPager.setCurrentItem(selectedPage, false)
        binding.viewPager.registerOnPageChangeCallback(pageCallback)
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putInt(KEY_SELECTED_PAGE, selectedPage)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {

        binding.viewPager.unregisterOnPageChangeCallback(pageCallback)
        mediator?.detach()
        mediator = null
        _binding = null
        super.onDestroyView()
    }
}
