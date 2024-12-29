package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.models.Track

interface SearchContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showTracks(foundTracks: List<Track>)
        fun showNotFound()
        fun hideNotFound()
        fun showNoService()
        fun hideNoService()
        fun showSearchList()
        fun hideSearchList()
        fun showHistory()
        fun hideHistory()
        fun enableClearButton(isEnabled: Boolean)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onQueryChanged(query: String)
        fun onClearClicked()
        fun onSearchAction()
        fun showHistory()
        fun hideHistory()
        fun refreshCurrentSearch()
    }
}