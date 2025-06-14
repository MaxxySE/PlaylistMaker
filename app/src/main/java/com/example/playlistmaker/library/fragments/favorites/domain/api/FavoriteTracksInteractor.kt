package com.example.playlistmaker.library.fragments.favorites.domain.api

import com.example.playlistmaker.sharing.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun isTrackFavorite(trackId: Int): Boolean
}