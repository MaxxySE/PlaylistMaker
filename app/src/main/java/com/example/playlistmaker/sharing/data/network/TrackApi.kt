package com.example.playlistmaker.sharing.data.network

import com.example.playlistmaker.sharing.data.dto.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface TrackApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}