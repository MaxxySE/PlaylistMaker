package com.example.playlistmaker.sharing.data.repository

import com.example.playlistmaker.sharing.data.NetworkClient
import com.example.playlistmaker.sharing.data.dto.TrackRequest
import com.example.playlistmaker.sharing.data.dto.TrackResponse
import com.example.playlistmaker.sharing.domain.api.TrackRepository
import com.example.playlistmaker.sharing.domain.models.Track
import com.example.playlistmaker.sharing.data.dto.toDomain

class TrackRepositoryImpl(
    private val networkClient: NetworkClient
) : TrackRepository {

    override fun searchTracks(text: String): List<Track> {
        val response = networkClient.doRequest(TrackRequest(text))

        return if (response.resultCode == 200 && response is TrackResponse) {
            if (response.results.isEmpty()) {
                emptyList()
            } else {
                response.results.map { it.toDomain() }
            }
        } else {
            throw Exception("No network or server error. Code: ${response.resultCode}")
        }
    }
}
