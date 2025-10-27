package com.example.playlistmaker.ui.media.fragment

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private var pickedImageUri: Uri? = null
    private var isDirty: Boolean = false


    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedImageUri = uri
            with(binding) {
                ivCover.isVisible = true
                ivCover.setImageURI(uri)
                ivAddIcon.isGone = true
            }
            isDirty = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbar.setNavigationOnClickListener { tryClose() }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            tryClose()
        }


        binding.coverContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCreateEnabled()
                isDirty = isDirty || !s.isNullOrBlank()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etName.addTextChangedListener(watcher)
        binding.etDesc.addTextChangedListener(watcher)


        binding.btnCreate.setOnClickListener {

        }

        updateCreateEnabled()
    }

    private fun updateCreateEnabled() {
        binding.btnCreate.isEnabled = !binding.etName.text.isNullOrBlank()
    }

    private fun tryClose() {
        val nothingEntered = pickedImageUri == null &&
                binding.etName.text.isNullOrBlank() &&
                binding.etDesc.text.isNullOrBlank()

        if (nothingEntered) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.finish_creation_title)
                .setMessage(R.string.finish_creation_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.finish) { _, _ ->
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}