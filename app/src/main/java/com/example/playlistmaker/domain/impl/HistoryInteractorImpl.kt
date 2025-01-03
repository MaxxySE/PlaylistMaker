package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.HistoryRepository
import com.example.playlistmaker.domain.models.Track

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