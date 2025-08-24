package com.example.playlistmaker.ui.search.viewmodel

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: SearchHistoryInteractor,
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val TOKEN = Any()
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var latestQuery: String = ""

    private val state = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = state

    fun onTextChanged(text: String) {
        latestQuery = text
        mainHandler.removeCallbacksAndMessages(TOKEN)
        val r = Runnable { doSearch(text) }
        mainHandler.postAtTime(r, TOKEN, SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY)
    }

    fun forceSearch(text: String) {
        latestQuery = text
        mainHandler.removeCallbacksAndMessages(TOKEN)
        doSearch(text)
    }

    private fun doSearch(query: String) {
        if (query.isBlank()) {

            val history = historyInteractor.getHistory()
            state.postValue(SearchState.History(history))
            return
        }

        state.postValue(SearchState.Loading)

        tracksInteractor.searchTracks(query, object : TracksInteractor.Consumer {
            override fun consume(tracks: List<Track>) {
                mainHandler.post {
                    if (tracks.isEmpty()) {
                        state.value = SearchState.Empty("Ничего не найдено")
                    } else {
                        state.value = SearchState.Content(tracks)
                    }
                }
            }
        })
    }

    fun addToHistory(track: Track) = historyInteractor.addTrack(track)

    fun clearHistory() {
        historyInteractor.clearHistory()
        state.postValue(SearchState.History(emptyList()))
    }

    fun loadHistory() {
        state.postValue(SearchState.History(historyInteractor.getHistory()))
    }

    override fun onCleared() {
        mainHandler.removeCallbacksAndMessages(TOKEN)
        super.onCleared()
    }
}