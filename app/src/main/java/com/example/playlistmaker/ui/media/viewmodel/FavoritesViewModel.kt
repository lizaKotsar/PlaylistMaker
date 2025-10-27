package com.example.playlistmaker.ui.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import kotlinx.coroutines.launch


class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>(FavoritesState.Empty)
    fun observeState(): LiveData<FavoritesState> = _state

    init {
        viewModelScope.launch {
            favoritesInteractor.getFavorites()
                .collect { list ->
                    _state.postValue(
                        if (list.isEmpty()) FavoritesState.Empty
                        else FavoritesState.Content(list)
                    )
                }
        }
    }
}