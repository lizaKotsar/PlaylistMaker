package com.example.playlistmaker.ui.settings.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    private val themeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        (requireActivity().application as App).switchTheme(isChecked)
        viewModel.onThemeToggled(isChecked)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shareButton.setOnClickListener { viewModel.onShareAppClicked() }
        binding.supportButton.setOnClickListener { viewModel.onOpenSupportClicked() }
        binding.agreementButton.setOnClickListener { viewModel.onOpenTermsClicked() }

        // синхронизация свитча без смены темы
        viewModel.observeIsDark().observe(viewLifecycleOwner) { enabled ->
            binding.themeSwitcher.setOnCheckedChangeListener(null)
            if (binding.themeSwitcher.isChecked != enabled) {
                binding.themeSwitcher.isChecked = enabled
            }
            binding.themeSwitcher.setOnCheckedChangeListener(themeListener)
        }
        binding.themeSwitcher.setOnCheckedChangeListener(themeListener)

        // действия
        viewModel.shareText.observe(viewLifecycleOwner) { text ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(intent, null))
        }
        viewModel.openUrl.observe(viewLifecycleOwner) { url ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        viewModel.sendEmail.observe(viewLifecycleOwner) { data ->
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(data.email))
                putExtra(Intent.EXTRA_SUBJECT, data.subject)
                putExtra(Intent.EXTRA_TEXT, data.body)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        binding.themeSwitcher.setOnCheckedChangeListener(null)
        _binding = null
        super.onDestroyView()
    }
}
