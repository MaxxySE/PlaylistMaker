package com.example.playlistmaker.presentation.search

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track

class SearchPresenter(
    private val trackInteractor: TrackInteractor
) : SearchContract.Presenter {

    private var view: SearchContract.View? = null

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }

    private var currentQuery: String = ""

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun attachView(view: SearchContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
        handler.removeCallbacks(searchRunnable)
    }

    override fun onQueryChanged(query: String) {
        currentQuery = query

        if (query.isEmpty()) {
            handler.removeCallbacks(searchRunnable)
            view?.enableClearButton(false)
            view?.hideSearchList()
            view?.hideNotFound()
            view?.hideNoService()
            view?.showHistory()
            return
        }

        view?.enableClearButton(true)
        view?.hideHistory()
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onClearClicked() {
        currentQuery = ""
        view?.enableClearButton(false)
        view?.hideSearchList()
        view?.hideNotFound()
        view?.hideNoService()
        view?.showHistory()
        handler.removeCallbacks(searchRunnable)
    }

    override fun onSearchAction() {
        handler.removeCallbacks(searchRunnable)
        performSearch()
    }

    override fun showHistory() {
        view?.showHistory()
    }

    override fun hideHistory() {
        view?.hideHistory()
    }

    override fun refreshCurrentSearch() {
        if (currentQuery.isNotEmpty()) {
            performSearch()
        } else {
            view?.hideSearchList()
            view?.hideNotFound()
            view?.hideNoService()
            view?.showHistory()
        }
    }

    private fun performSearch() {
        val query = currentQuery.trim()
        if (query.isNotEmpty()) {
            view?.showLoading()
            view?.hideNotFound()
            view?.hideNoService()
            view?.hideSearchList()

            trackInteractor.searchTracks(query, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>) {
                    handler.post {
                        view?.hideLoading()
                        if (foundTracks.isEmpty()) {
                            view?.showNotFound()
                        } else {
                            view?.showTracks(foundTracks)
                            view?.showSearchList()
                        }
                    }
                }

                override fun onError(errorMessage: String) {
                    handler.post {
                        view?.hideLoading()
                        view?.showNoService()
                    }
                }
            })
        }
    }
}