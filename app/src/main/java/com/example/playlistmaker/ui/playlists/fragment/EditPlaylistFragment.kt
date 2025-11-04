package com.example.playlistmaker.ui.playlists.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.ui.playlists.viewmodel.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : Fragment(R.layout.fragment_create_playlist) {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val args: EditPlaylistFragmentArgs by navArgs()
    private val vm: EditPlaylistViewModel by viewModel { parametersOf(args.playlistId) }

    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            vm.pickedImageUri = uri
            vm.clearedCover = false
            renderCover(uri = uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreatePlaylistBinding.bind(view)

        // Заголовок и кнопка под режим редактирования
        binding.toolbar.title = "Редактировать"
        binding.btnCreate.text = "Сохранить"

        // Назад — просто закрываемся без сохранения
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp() // ничего не возвращаем
                }
            }
        )

        // Подписки
        vm.initialData.observe(viewLifecycleOwner) { data ->
            binding.etName.setText(data.name)
            binding.etName.setSelection(binding.etName.text?.length ?: 0)
            binding.etDesc.setText(data.description.orEmpty())
            renderCover(path = data.coverPath)
        }
        vm.isSaveEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.btnCreate.isEnabled = enabled
        }
        vm.close.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        // Изменения полей
        binding.etName.doAfterTextChanged { vm.onNameChanged(it) }
        binding.etDesc.doAfterTextChanged { vm.onDescriptionChanged(it) }

        // Выбор/очистка обложки
        binding.coverContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
        binding.ivCover.setOnLongClickListener {
            vm.onClearCover()
            renderCover(uri = null) // <— явно укажем перегрузку
            true
        }

        // Сохранить
        binding.btnCreate.setOnClickListener { vm.save() }
    }

    private fun renderCover(uri: Uri?) {
        if (uri == null) {
            binding.ivCover.setImageDrawable(null)
            binding.ivCover.isGone = true
            binding.ivAddIcon.isVisible = true
            binding.coverContainer.setBackgroundResource(R.drawable.bg_cover_placeholder_dashed)
        } else {
            binding.ivCover.isVisible = true
            binding.ivAddIcon.isGone = true
            binding.coverContainer.background = null
            binding.ivCover.setImageURI(uri)
        }
    }

    private fun renderCover(path: String?) {
        if (path.isNullOrBlank()) {
            renderCover(uri = null)
        } else {
            binding.ivCover.isVisible = true
            binding.ivAddIcon.isGone = true
            binding.coverContainer.background = null
            Glide.with(binding.ivCover)
                .load(path)
                .centerCrop()
                .into(binding.ivCover)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}