package com.example.playlistmaker.ui.media.viewmodel


import androidx.lifecycle.*
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistsInteractor
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = interactor.getAll()
            _playlists.postValue(data)
        }
    }
}