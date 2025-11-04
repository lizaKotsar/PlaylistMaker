package com.example.playlistmaker.ui.playlists.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class EditPlaylistViewModel(
    private val interactor: PlaylistsInteractor,
    private val appContext: Context,
    private val playlistId: Long
) : ViewModel() {

    data class InitialData(
        val name: String,
        val description: String?,
        val coverPath: String?
    )

    val initialData = MutableLiveData<InitialData>()
    val isSaveEnabled = MutableLiveData(false)
    val close = MutableLiveData<Unit>()

    private var original: Playlist? = null

    var pickedImageUri: Uri? = null          // новая выбранная обложка (если выберут)
    var clearedCover: Boolean = false        // пользователь очистил обложку долгим нажатием

    var name: String = ""
    var description: String = ""

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val pl = interactor.getPlaylist(playlistId) ?: return@launch
            original = pl
            name = pl.name
            description = pl.description.orEmpty()
            isSaveEnabled.value = name.isNotBlank()
            initialData.value = InitialData(
                name = pl.name,
                description = pl.description,
                coverPath = pl.coverPath
            )
        }
    }

    fun onNameChanged(text: CharSequence?) {
        name = text?.toString().orEmpty()
        isSaveEnabled.value = name.isNotBlank()
    }

    fun onDescriptionChanged(text: CharSequence?) {
        description = text?.toString().orEmpty()
    }

    fun onClearCover() {
        pickedImageUri = null
        clearedCover = true
    }

    fun save() {
        val old = original ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val newCoverPath: String? = when {
                clearedCover -> null
                pickedImageUri != null -> saveImageToPrivateStorage(pickedImageUri!!)
                else -> old.coverPath
            }
            val updated = old.copy(
                name = name.trim(),
                description = description.trim().ifBlank { null },
                coverPath = newCoverPath
            )
            interactor.update(updated)
            close.postValue(Unit)
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String? {
        val dir = File(
            appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlists"
        )
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "pl_${System.currentTimeMillis()}.jpg")
        appContext.contentResolver.openInputStream(uri)?.use { ins ->
            FileOutputStream(file).use { outs ->
                val bmp: Bitmap = BitmapFactory.decodeStream(ins)
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outs)
            }
        }
        return file.absolutePath
    }
}