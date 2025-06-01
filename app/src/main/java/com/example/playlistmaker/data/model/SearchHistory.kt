package com.example.playlistmaker.data.model

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()

    fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveTrack(track: Track) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)
        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeLast()
        }
        saveHistory(currentHistory)
    }

    fun clearHistory() {
        sharedPrefs.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }
}