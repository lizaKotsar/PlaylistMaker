package com.example.playlistmaker.ui.media.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.ui.playlists.viewmodel.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment(R.layout.fragment_create_playlist) {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val vm: CreatePlaylistViewModel by viewModel()

    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            vm.pickedImageUri = uri
            binding.ivCover.visibility = View.VISIBLE
            binding.ivCover.setImageURI(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreatePlaylistBinding.bind(view)


        binding.toolbar.setNavigationOnClickListener { handleBack() }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = handleBack()
            }
        )


        binding.coverContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }


        val nameEt = binding.tilName.editText!!
        val descEt = binding.tilDesc.editText!!

        nameEt.doAfterTextChanged { vm.onNameChanged(it) }
        descEt.doAfterTextChanged { vm.onDescriptionChanged(it) }


        vm.isCreateEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.btnCreate.isEnabled = enabled
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
    }

    private fun handleBack() {
        val hasInput = vm.name.isNotBlank() || vm.description.isNotBlank() || vm.pickedImageUri != null
        if (!hasInput) {
            findNavController().navigateUp()
        } else {
            MaterialAlertDialogBuilder(requireContext(), R.style.AppAlertDialogTheme)
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