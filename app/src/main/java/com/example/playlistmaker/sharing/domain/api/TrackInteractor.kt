package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.models.Track

import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>>
}
