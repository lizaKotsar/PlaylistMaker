package com.example.playlistmaker.ui.playlists.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

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

    private val _finish = MutableLiveData<Unit>()
    val finish: LiveData<Unit> = _finish

    private var current: Playlist? = null
    private var lastTracks: List<Track> = emptyList()

    fun load(playlistId: Long) {
        _state.value = State.Loading
        viewModelScope.launch {
            try {
                val pl = interactor.getPlaylist(playlistId)
                if (pl == null) {
                    _state.value = State.Empty
                    return@launch
                }
                current = pl
                val tracks = interactor.getTracksForPlaylist(playlistId)
                lastTracks = tracks
                val totalMs = tracks.sumOf { it.trackTimeMillis ?: 0L }
                val minutes = (totalMs / 1000L / 60L).toInt()
                _state.value = State.Content(pl, tracks, minutes)
            } catch (e: Throwable) {
                _state.value = State.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun removeTrack(track: Track) {
        val pl = current ?: return
        val id = track.trackId ?: return
        viewModelScope.launch {
            interactor.removeTrackFromPlaylist(pl, id)
            load(pl.id) // перезагрузим состояние
        }
    }

    fun buildShareText(): String? {
        val pl = current ?: return null
        if (lastTracks.isEmpty()) return null

        val mmss = SimpleDateFormat("mm:ss", Locale.getDefault())
        val sb = StringBuilder()
        sb.append(pl.name).append('\n')
        pl.description?.takeIf { it.isNotBlank() }?.let {
            sb.append(it.trim()).append('\n')
        }
        sb.append("${lastTracks.size} треков").append('\n')
        lastTracks.forEachIndexed { index, t ->
            val d = mmss.format(t.trackTimeMillis ?: 0L)
            sb.append("${index + 1}. ${t.artistName} - ${t.trackName} ($d)")
            if (index != lastTracks.lastIndex) sb.append('\n')
        }
        return sb.toString()
    }

    fun deletePlaylist() {
        val pl = current ?: return
        viewModelScope.launch {
            interactor.deletePlaylist(pl.id)
            _finish.postValue(Unit)
        }
    }
}