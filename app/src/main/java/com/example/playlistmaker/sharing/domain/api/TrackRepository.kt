package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.models.Track
import kotlinx.coroutines.flow.Flow

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}

interface TrackRepository {
    fun searchTracks(text: String): Flow<Resource<List<Track>>>
}