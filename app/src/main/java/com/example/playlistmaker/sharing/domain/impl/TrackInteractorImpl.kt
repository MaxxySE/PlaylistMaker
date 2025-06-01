package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.Resource
import com.example.playlistmaker.sharing.domain.api.TrackInteractor
import com.example.playlistmaker.sharing.domain.api.TrackRepository
import com.example.playlistmaker.sharing.domain.models.Track

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(
    private val repository: TrackRepository
) : TrackInteractor {

    override fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(text).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}