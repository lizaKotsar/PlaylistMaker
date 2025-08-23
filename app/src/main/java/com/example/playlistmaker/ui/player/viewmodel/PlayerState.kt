package com.example.playlistmaker.ui.player.viewmodel

data class PlayerState(
    val isPlayEnabled: Boolean = false,
    val isPlaying: Boolean = false,
    val timerText: String = "00:00"
)