package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.sharing.domain.models.Track

interface HistoryInteractor {
    fun getHistory(): List<Track>
    fun saveTrack(track: Track)
    fun clearHistory()
}