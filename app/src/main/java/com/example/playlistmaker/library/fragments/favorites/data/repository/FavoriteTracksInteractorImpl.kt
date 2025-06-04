package com.example.playlistmaker.library.fragments.favorites.data.repository

import com.example.playlistmaker.library.fragments.favorites.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.library.fragments.favorites.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.sharing.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        return repository.getFavoriteTrackIds().contains(trackId)
    }
}