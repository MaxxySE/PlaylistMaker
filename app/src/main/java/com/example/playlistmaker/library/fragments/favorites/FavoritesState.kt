package com.example.playlistmaker.library.fragments.favorites

import com.example.playlistmaker.sharing.domain.models.Track

sealed interface FavoritesState {
    data object Empty : FavoritesState 
    data class Content(val tracks: List<Track>) : FavoritesState
}