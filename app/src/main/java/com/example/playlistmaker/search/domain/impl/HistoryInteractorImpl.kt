package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.sharing.domain.models.Track

class HistoryInteractorImpl(
    private val repository: HistoryRepository
) : HistoryInteractor {

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}