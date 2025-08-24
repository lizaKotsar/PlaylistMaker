package com.example.playlistmaker.ui.player.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.player.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val _state = MutableLiveData(PlayerState())
    fun observeState(): LiveData<PlayerState> = _state

    private val handler = Handler(Looper.getMainLooper())
    private val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (_state.value?.isPlaying == true) {
                val pos = playerInteractor.getPositionMs()
                _state.postValue(_state.value?.copy(timerText = formatter.format(pos)))
                handler.postDelayed(this, 300L)
            }
        }
    }

    fun prepare(url: String?) {
        if (url.isNullOrEmpty()) {
            _state.postValue(_state.value?.copy(isPlayEnabled = false, timerText = "00:00"))
            return
        }
        _state.postValue(_state.value?.copy(isPlayEnabled = false, timerText = "00:00"))

        playerInteractor.prepare(
            url,
            onPrepared = {
                _state.postValue(_state.value?.copy(isPlayEnabled = true, isPlaying = false, timerText = "00:00"))
            },
            onCompletion = {
                stopProgress()
                _state.postValue(_state.value?.copy(isPlaying = false, isPlayEnabled = true, timerText = "00:00"))
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
        _state.postValue(_state.value?.copy(isPlaying = true))
        startProgress()
    }

    private fun pause() {
        playerInteractor.pause()
        _state.postValue(_state.value?.copy(isPlaying = false))
        stopProgress()
    }

    fun onPause() {
        if (_state.value?.isPlaying == true) pause()
    }

    private fun startProgress() {
        handler.removeCallbacks(progressRunnable)
        handler.post(progressRunnable)
    }

    private fun stopProgress() {
        handler.removeCallbacks(progressRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        stopProgress()
        playerInteractor.release()
    }
}