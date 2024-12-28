package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(
    private val repository: TrackRepository
) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(text: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            try {
                val tracks = repository.findTracks(text)
                if (tracks.isEmpty()) {
                    consumer.consume(emptyList())
                } else {
                    consumer.consume(tracks)
                }
            } catch (e: Exception) {
                consumer.onError(e.message ?: "Unknown error")
            }
        }
    }
}