package com.example.playlistmaker.search.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.sharing.domain.api.TrackInteractor

class SearchViewModelFactory(
    private val application: Application,
    private val trackInteractor: TrackInteractor
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(application, trackInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
