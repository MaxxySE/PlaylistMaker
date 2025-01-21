package com.example.playlistmaker.sharing.data.repository

import com.example.playlistmaker.sharing.data.NetworkClient
import com.example.playlistmaker.sharing.data.dto.TrackRequest
import com.example.playlistmaker.sharing.data.dto.TrackResponse
import com.example.playlistmaker.sharing.domain.api.TrackRepository
import com.example.playlistmaker.sharing.domain.models.Track

class TrackRepositoryImpl(
    private val networkClient: NetworkClient
) : TrackRepository {

    override fun searchTracks(text: String): List<Track> {
        val response = networkClient.doRequest(TrackRequest(text))

        return if (response.resultCode == 200 && response is TrackResponse) {
            if (response.results.isEmpty()) {
                emptyList()
            } else {
                // TrackDto -> Track
                response.results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl
                    )
                }
            }
        } else {
            throw Exception("No network or server error. Code: ${response.resultCode}")
        }
    }
}
