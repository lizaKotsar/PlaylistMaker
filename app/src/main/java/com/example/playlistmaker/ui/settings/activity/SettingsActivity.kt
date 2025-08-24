package com.example.playlistmaker.ui.settings.activity

import android.content.Intent
import android.net.Uri
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


        viewModel.shareText.observe(this) { text ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(intent, null))
        }

        viewModel.openUrl.observe(this) { url ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        viewModel.sendEmail.observe(this) { data ->
            val uri = Uri.parse("mailto:")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(data.email))
                putExtra(Intent.EXTRA_SUBJECT, data.subject)
                putExtra(Intent.EXTRA_TEXT, data.body)
            }
            startActivity(intent)
        }


        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeToggled(checked)
        }
        shareButton.setOnClickListener { viewModel.onShareAppClicked() }
        supportButton.setOnClickListener { viewModel.onOpenSupportClicked() }
        agreementButton.setOnClickListener { viewModel.onOpenTermsClicked() }
        backButton.setOnClickListener { finish() }
    }
}
