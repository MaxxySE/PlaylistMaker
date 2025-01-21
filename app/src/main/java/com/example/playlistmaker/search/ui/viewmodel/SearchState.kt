package com.example.playlistmaker.search.ui.viewmodel

import com.example.playlistmaker.sharing.domain.models.Track

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    object NotFound : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class Error(val message: String) : SearchState()
    object ShowHistory : SearchState()
}
