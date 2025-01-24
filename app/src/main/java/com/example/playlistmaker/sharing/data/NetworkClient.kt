package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.sharing.data.dto.ApiResponse

interface NetworkClient {
    fun doRequest(dto: Any): ApiResponse
}
