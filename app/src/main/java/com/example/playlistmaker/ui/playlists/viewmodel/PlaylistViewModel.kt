package com.example.playlistmaker.ui.playlists.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistsInteractor
) : ViewModel() {

    sealed class State {
        data object Loading : State()
        data class Content(
            val playlist: Playlist,
            val tracks: List<Track>,
            val totalMinutes: Int
        ) : State()
        data object Empty : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun load(playlistId: Long) {
        _state.value = State.Loading
        viewModelScope.launch {
            try {
                val pl = interactor.getPlaylist(playlistId)
                if (pl == null) {
                    _state.value = State.Empty
                    return@launch
                }

                val tracks = interactor.getTracksForPlaylist(playlistId)
                val totalMs = tracks.sumOf { it.trackTimeMillis ?: 0L }
                val minutes = (totalMs / 1000L / 60L).toInt()

                _state.value = State.Content(
                    playlist = pl,
                    tracks = tracks,
                    totalMinutes = minutes
                )
            } catch (e: Throwable) {
                _state.value = State.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }
}