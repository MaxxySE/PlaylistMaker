package com.example.playlistmaker.library.fragments.favorites.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.fragments.favorites.FavoritesState
import com.example.playlistmaker.library.fragments.favorites.domain.api.FavoriteTracksInteractor
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    init {
        loadFavoriteTracks()
    }

    private fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        _state.postValue(FavoritesState.Empty)
                    } else {
                        _state.postValue(FavoritesState.Content(tracks))
                    }
                }
        }
    }

    fun onResume() {
        loadFavoriteTracks()
    }
}
