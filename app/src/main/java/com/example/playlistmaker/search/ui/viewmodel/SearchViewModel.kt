package com.example.playlistmaker.search.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.sharing.domain.api.TrackInteractor
import com.example.playlistmaker.sharing.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val trackInteractor: TrackInteractor
) : AndroidViewModel(application) {

    private val _state = MutableLiveData<SearchState>(SearchState.Idle)
    val state: LiveData<SearchState> = _state

    private var searchJob: Job? = null
    private var currentQuery: String = ""

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun onQueryChanged(query: String) {
        currentQuery = query
        searchJob?.cancel()

        if (query.isBlank()) {
            _state.value = SearchState.ShowHistory
            return
        }

        _state.value = SearchState.Loading
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(query.trim())
        }
    }

    fun onClearClicked() {
        currentQuery = ""
        searchJob?.cancel()
        _state.value = SearchState.ShowHistory
    }

    fun onSearchAction() {
        searchJob?.cancel()
        performSearch(currentQuery.trim())
    }

    fun refreshCurrentSearch() {
        if (currentQuery.isNotEmpty()) {
            performSearch(currentQuery.trim())
        } else {
            _state.value = SearchState.ShowHistory
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            _state.value = SearchState.ShowHistory
            return
        }

        _state.value = SearchState.Loading

        trackInteractor.searchTracks(query, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>) {
                if (foundTracks.isEmpty()) {
                    _state.postValue(SearchState.NotFound)
                } else {
                    _state.postValue(SearchState.Content(foundTracks))
                }
            }

            override fun onError(errorMessage: String) {
                _state.postValue(SearchState.Error(errorMessage))
            }
        })
    }

    fun showHistory() {
        _state.value = SearchState.ShowHistory
    }

    fun hideHistory() {
        _state.value = SearchState.Idle
    }
}
