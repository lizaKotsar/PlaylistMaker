package com.example.playlistmaker.ui.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.common.ResourceProvider
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: SearchHistoryInteractor,
    private val resources: ResourceProvider,
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var latestQuery: String = ""

    private val state = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = state


    private var searchJob: Job? = null

    fun onTextChanged(text: String) {
        latestQuery = text
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            doSearch(text)
        }
    }

    fun forceSearch(text: String) {
        latestQuery = text
        searchJob?.cancel()
        doSearch(text)
    }

    private fun doSearch(query: String) {
        if (query.isBlank()) {
            val history = historyInteractor.getHistory()
            state.postValue(SearchState.History(history))
            return
        }

        state.postValue(SearchState.Loading)


        tracksInteractor.searchTracks(query, TracksInteractor.Consumer { tracks ->
            if (tracks.isEmpty()) {
                state.postValue(SearchState.Empty(resources.getString(R.string.nothing_found)))
            } else {
                state.postValue(SearchState.Content(tracks))
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
}