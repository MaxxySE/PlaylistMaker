package com.example.playlistmaker.library.fragments.favorites.domain.api

import com.example.playlistmaker.sharing.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun getFavoriteTrackIds(): List<Int>
}