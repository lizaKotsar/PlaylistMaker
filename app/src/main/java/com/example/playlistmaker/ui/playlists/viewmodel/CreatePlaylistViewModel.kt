package com.example.playlistmaker.ui.playlists.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.*
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistViewModel(
    private val interactor: PlaylistsInteractor,
    private val appContext: Context
) : ViewModel() {

    val isCreateEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val closeWithSuccess: MutableLiveData<String> = MutableLiveData()

    var pickedImageUri: Uri? = null
    var name: String = ""
    var description: String = ""

    fun onNameChanged(text: CharSequence?) {
        name = text?.toString().orEmpty()
        isCreateEnabled.value = name.isNotBlank()
    }

    fun onDescriptionChanged(text: CharSequence?) {
        description = text?.toString().orEmpty()
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            val coverPath = pickedImageUri?.let { saveImageToPrivateStorage(it) }
            interactor.create(name.trim(), description.trim().ifBlank { null }, coverPath)
            closeWithSuccess.postValue(name.trim())
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