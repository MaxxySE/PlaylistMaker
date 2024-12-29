package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.ApiResponse

interface NetworkClient {
    fun doRequest(dto: Any): ApiResponse
}
