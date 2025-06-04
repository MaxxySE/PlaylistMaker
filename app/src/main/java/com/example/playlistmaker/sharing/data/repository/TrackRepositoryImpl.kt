package com.example.playlistmaker.sharing.data.repository

import android.util.Log
import com.example.playlistmaker.library.fragments.favorites.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.sharing.data.NetworkClient
import com.example.playlistmaker.sharing.data.dto.TrackRequest
import com.example.playlistmaker.sharing.data.dto.TrackResponse
import com.example.playlistmaker.sharing.data.dto.toDomain
import com.example.playlistmaker.sharing.domain.api.Resource
import com.example.playlistmaker.sharing.domain.api.TrackRepository
import com.example.playlistmaker.sharing.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favoriteTracksRepository: FavoriteTracksRepository
) : TrackRepository {

    override fun searchTracks(text: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackRequest(text))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                val favoriteIds = favoriteTracksRepository.getFavoriteTrackIds()
                Log.d("TrackRepo_LOG", "Favorite IDs from DB: $favoriteIds")

                val data = (response as TrackResponse).results.map { trackDto ->
                    val domainTrack = trackDto.toDomain()
                    domainTrack.isFavorite = favoriteIds.contains(domainTrack.trackId)
                    Log.d("TrackRepo_LOG", "Track: ${domainTrack.trackName}, isFavorite set to: ${domainTrack.isFavorite}")
                    domainTrack
                }
                emit(Resource.Success(data))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}

