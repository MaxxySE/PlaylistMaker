package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.HistoryTrackDto
import com.example.playlistmaker.data.local.SearchHistorySource
import com.example.playlistmaker.domain.api.HistoryRepository
import com.example.playlistmaker.domain.models.Track

class HistoryRepositoryImpl(
    private val searchHistoryDataSource: SearchHistorySource
) : HistoryRepository {

    override fun getHistory(): List<Track> {
        val dtoList = searchHistoryDataSource.readTrackList()
        return dtoList.map { it.toDomain() }
    }

    override fun saveTrack(track: Track) {
        val dtoList = searchHistoryDataSource.readTrackList().toMutableList()

        dtoList.removeAll { it.trackId == track.trackId }

        if (dtoList.size == 10) {
            dtoList.removeLast()
        }

        dtoList.add(0, track.toDto())

        searchHistoryDataSource.saveTrackList(dtoList)
    }

    override fun clearHistory() {
        searchHistoryDataSource.clear()
    }

    private fun HistoryTrackDto.toDomain(): Track {
        return Track(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            artworkUrl100 = this.artworkUrl100,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            previewUrl = this.previewUrl
        )
    }

    private fun Track.toDto(): HistoryTrackDto {
        return HistoryTrackDto(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            artworkUrl100 = this.artworkUrl100,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            previewUrl = this.previewUrl
        )
    }
}