package com.example.playlistmaker.ui.playlists.fragment

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.ui.playlists.viewmodel.CreatePlaylistViewModel
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment(R.layout.fragment_create_playlist) {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val vm: CreatePlaylistViewModel by viewModel()

    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            vm.pickedImageUri = uri
            renderCover(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreatePlaylistBinding.bind(view)
        binding.toolbar.setNavigationOnClickListener { handleBack() }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) { override fun handleOnBackPressed() = handleBack() }
        )


        binding.etName.setText(vm.name)
        binding.etName.setSelection(binding.etName.text?.length ?: 0)
        binding.etDesc.setText(vm.description)

        renderCover(vm.pickedImageUri)
        updateCreateButton(vm.isCreateEnabled.value == true)


        binding.coverContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        binding.etName.doAfterTextChanged { vm.onNameChanged(it) }
        binding.etDesc.doAfterTextChanged { vm.onDescriptionChanged(it) }

        vm.isCreateEnabled.observe(viewLifecycleOwner) { enabled ->
            updateCreateButton(enabled)
        }

        binding.btnCreate.setOnClickListener { vm.save() }

        vm.closeWithSuccess.observe(viewLifecycleOwner) { name ->
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created_toast, name),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }


        binding.ivCover.setOnLongClickListener {
            vm.pickedImageUri = null
            renderCover(null)
            true
        }
    }

    private fun renderCover(uri: Uri?) {
        if (uri == null) {

            binding.ivCover.setImageDrawable(null)
            binding.ivCover.isGone = true
            binding.ivAddIcon.isVisible = true
            binding.coverContainer.setBackgroundResource(R.drawable.bg_cover_placeholder_dashed)
        } else {

            binding.ivCover.isVisible = true
            binding.ivCover.setImageURI(uri)
            binding.ivCover.bringToFront()
            binding.ivAddIcon.isGone = true
            binding.coverContainer.background = null
        }
    }

    private fun updateCreateButton(enabled: Boolean) {
        binding.btnCreate.isEnabled = enabled

        val primary    = MaterialColors.getColor(binding.btnCreate, com.google.android.material.R.attr.colorPrimary)
        val onPrimary  = MaterialColors.getColor(binding.btnCreate, com.google.android.material.R.attr.colorOnPrimary)
        val disabledBg = ContextCompat.getColor(requireContext(), R.color.YP_Text_Gray)
        val disabledTx = MaterialColors.getColor(binding.btnCreate, com.google.android.material.R.attr.colorOnSecondary)

        binding.btnCreate.backgroundTintList = ColorStateList.valueOf(if (enabled) primary else disabledBg)
        binding.btnCreate.setTextColor(if (enabled) onPrimary else disabledTx)
    }

    private fun handleBack() {
        val hasInput = vm.name.isNotBlank() || vm.description.isNotBlank() || vm.pickedImageUri != null
        if (!hasInput) {
            findNavController().navigateUp()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.finish_creation_title)
                .setMessage(R.string.finish_creation_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.finish) { _, _ -> findNavController().navigateUp() }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}