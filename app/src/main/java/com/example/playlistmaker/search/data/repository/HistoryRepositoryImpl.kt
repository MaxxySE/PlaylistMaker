package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.dto.HistoryTrackDto
import com.example.playlistmaker.search.data.local.SearchHistorySource
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.sharing.domain.models.Track
import com.example.playlistmaker.search.data.dto.toDomain
import com.example.playlistmaker.search.data.dto.toHistoryTrackDto

class HistoryRepositoryImpl(
    private val searchHistoryDataSource: SearchHistorySource
) : HistoryRepository {

    override fun getHistory(): List<Track> {
        val dtoList: List<HistoryTrackDto> = searchHistoryDataSource.readTrackList()
        return dtoList.map { it.toDomain() }
    }

    override fun saveTrack(track: Track) {
        val dtoList = searchHistoryDataSource.readTrackList().toMutableList()

        dtoList.removeAll { it.trackId == track.trackId }

        dtoList.add(0, track.toHistoryTrackDto())

        if (dtoList.size > 10) {
            dtoList.removeAt(dtoList.lastIndex)
        }

        searchHistoryDataSource.saveTrackList(dtoList)
    }

    override fun clearHistory() {
        searchHistoryDataSource.clear()
    }
}