package com.example.playlistmaker.ui.player.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.common.ResourceProvider
import com.example.playlistmaker.domain.player.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _state = MutableLiveData(PlayerState())
    fun observeState(): LiveData<PlayerState> = _state

    private val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    private var timerJob: Job? = null

    fun prepare(url: String?) {
        val time = resourceProvider.getString(R.string.time)

        if (url.isNullOrEmpty()) {
            _state.postValue(_state.value?.copy(isPlayEnabled = false, timerText = time))
            return
        }

        _state.postValue(_state.value?.copy(isPlayEnabled = false, timerText = time))

        playerInteractor.prepare(
            url,
            onPrepared = {
                _state.postValue(
                    _state.value?.copy(isPlayEnabled = true, isPlaying = false, timerText = time)
                )
            },
            onCompletion = {
                stopTimer()
                _state.postValue(
                    _state.value?.copy(isPlaying = false, isPlayEnabled = true, timerText = time)
                )
            }
        )
    }

    fun onPlayPauseClicked() {
        val current = _state.value ?: return
        when {
            current.isPlaying -> pause()
            current.isPlayEnabled -> play()
        }
    }

    private fun play() {
        playerInteractor.play()
        _state.value = _state.value?.copy(isPlaying = true)
        startTimer()
    }

    private fun pause() {
        playerInteractor.pause()
        _state.value = _state.value?.copy(isPlaying = false)
        stopTimer()
    }

    fun onPause() {
        if (_state.value?.isPlaying == true) pause()
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                val posMs = playerInteractor.getPositionMs().toLong()
                _state.postValue(_state.value?.copy(timerText = formatter.format(posMs)))
                delay(300L)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        stopTimer()
        playerInteractor.release()
        super.onCleared()
    }
}