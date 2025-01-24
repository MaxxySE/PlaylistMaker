package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.models.Track

interface TrackInteractor {
    fun searchTracks(text: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
        fun onError(errorMessage: String)
    }
}
