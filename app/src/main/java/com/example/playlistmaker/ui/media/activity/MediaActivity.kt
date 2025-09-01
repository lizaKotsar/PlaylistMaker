package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.ui.media.adapter.MediaAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var mediator: TabLayoutMediator

    private companion object {
        const val KEY_SELECTED_PAGE = "media_selected_page"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.viewPager.adapter = MediaAdapter(
            supportFragmentManager,
            lifecycle
        )


        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> getString(R.string.favorites_tab)
                else -> getString(R.string.playlists_tab)
            }
        }
        mediator.attach()


        val index = savedInstanceState?.getInt(KEY_SELECTED_PAGE) ?: 0
        binding.viewPager.setCurrentItem(index, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_PAGE, binding.viewPager.currentItem)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        if (::mediator.isInitialized) mediator.detach()
        super.onDestroy()
    }
}