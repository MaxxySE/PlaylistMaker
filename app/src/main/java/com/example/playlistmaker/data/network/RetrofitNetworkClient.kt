package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ApiResponse
import com.example.playlistmaker.data.dto.TrackRequest


class RetrofitNetworkClient(
    private val trackApi: TrackApi
) : NetworkClient {

    override fun doRequest(dto: Any): ApiResponse {
        return if (dto is TrackRequest) {
            try {
                val response = trackApi.search(dto.text).execute()
                val body = response.body() ?: ApiResponse()
                body.resultCode = response.code()
                body
            } catch (e: Exception) {
                ApiResponse().apply {
                    resultCode = 500
                }
            }
        } else {
            ApiResponse().apply { resultCode = 400 }
        }
    }
}