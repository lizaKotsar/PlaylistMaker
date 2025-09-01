package com.example.playlistmaker.ui.settings.fragment


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


    private val themeListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeToggled(isChecked)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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


        viewModel.observeIsDark().observe(viewLifecycleOwner) { enabled ->

            binding.themeSwitcher.setOnCheckedChangeListener(null)
            if (binding.themeSwitcher.isChecked != enabled) {
                binding.themeSwitcher.isChecked = enabled
            }
            binding.themeSwitcher.setOnCheckedChangeListener(themeListener)


            (requireContext().applicationContext as App).switchTheme(enabled)
        }


        binding.themeSwitcher.setOnCheckedChangeListener(themeListener)


        viewModel.shareText.observe(viewLifecycleOwner) { text ->

        }
        viewModel.openUrl.observe(viewLifecycleOwner) { url ->

        }
        viewModel.sendEmail.observe(viewLifecycleOwner) { data ->

        }
    }

    override fun onDestroyView() {

        binding.themeSwitcher.setOnCheckedChangeListener(null)
        _binding = null
        super.onDestroyView()
    }
}