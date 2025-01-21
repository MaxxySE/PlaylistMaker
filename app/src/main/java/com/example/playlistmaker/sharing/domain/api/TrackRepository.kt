package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.models.Track

interface TrackRepository {
    fun searchTracks(text: String): List<Track>
}
