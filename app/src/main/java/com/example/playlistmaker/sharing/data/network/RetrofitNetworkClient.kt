package com.example.playlistmaker.sharing.data.network

import com.example.playlistmaker.sharing.data.NetworkClient
import com.example.playlistmaker.sharing.data.dto.ApiResponse
import com.example.playlistmaker.sharing.data.dto.TrackRequest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val trackApi: TrackApi
) : NetworkClient {

    override suspend fun doRequest(dto: Any): ApiResponse {

        if (dto !is TrackRequest) {
            return ApiResponse().apply { resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = trackApi.search(dto.text)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                ApiResponse().apply { resultCode = 500 } // Ошибка сервера/запроса
            }
        }
    }
}