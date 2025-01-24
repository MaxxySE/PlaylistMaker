package com.example.playlistmaker.search.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.sharing.domain.api.TrackInteractor
import com.example.playlistmaker.sharing.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val trackInteractor: TrackInteractor,
    private val historyInteractor: HistoryInteractor
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
            showHistory()
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
        showHistory()
    }

    fun onSearchAction() {
        searchJob?.cancel()
        performSearch(currentQuery.trim())
    }

    fun refreshCurrentSearch() {
        if (currentQuery.isNotEmpty()) {
            performSearch(currentQuery.trim())
        } else {
            showHistory()
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            showHistory()
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
        val history = historyInteractor.getHistory()
        _state.value = SearchState.ShowHistory(history)
    }

    fun hideHistory() {
        _state.value = SearchState.Idle
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        showHistory()
    }

    fun saveTrackToHistory(track: Track) {
        historyInteractor.saveTrack(track)
    }
}
