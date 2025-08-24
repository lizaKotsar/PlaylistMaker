package com.example.playlistmaker.ui.player.viewmodel

import java.text.SimpleDateFormat
import java.util.Locale

object TimeFormats {
    private val mmss = SimpleDateFormat("mm:ss", Locale.getDefault())
    fun mmss(ms: Long): String = mmss.format(ms)
}