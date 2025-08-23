package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom); insets
        }


        viewModel = ViewModelProvider(
            this,
            Creator.provideSettingsViewModelFactory(this)
        ).get(SettingsViewModel::class.java)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val shareButton = findViewById<LinearLayout>(R.id.share_button)
        val supportButton = findViewById<LinearLayout>(R.id.support_button)
        val agreementButton = findViewById<LinearLayout>(R.id.agreement_button)
        val backButton = findViewById<ImageButton>(R.id.back_button)


        viewModel.observeIsDark().observe(this) { enabled ->

            if (themeSwitcher.isChecked != enabled) themeSwitcher.isChecked = enabled
            (applicationContext as App).switchTheme(enabled)
        }


        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeToggled(checked)
        }
        shareButton.setOnClickListener { viewModel.shareApp() }
        supportButton.setOnClickListener { viewModel.openSupport() }
        agreementButton.setOnClickListener { viewModel.openTerms() }
        backButton.setOnClickListener { finish() }
    }
}