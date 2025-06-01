package com.example.playlistmaker.sharing.data.repository

import com.example.playlistmaker.sharing.data.NetworkClient
import com.example.playlistmaker.sharing.data.dto.TrackRequest
import com.example.playlistmaker.sharing.data.dto.TrackResponse
import com.example.playlistmaker.sharing.domain.api.TrackRepository
import com.example.playlistmaker.sharing.domain.models.Track
import com.example.playlistmaker.sharing.data.dto.toDomain
import com.example.playlistmaker.sharing.domain.api.Resource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient
) : TrackRepository {

    override fun searchTracks(text: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackRequest(text))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                val data = (response as TrackResponse).results.map { it.toDomain() }
                emit(Resource.Success(data))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}

