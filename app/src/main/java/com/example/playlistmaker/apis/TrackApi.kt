package com.example.playlistmaker.apis

import com.example.playlistmaker.responses.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApi {

    @GET("/search?entity=song?term=")
    fun search(@Query("term") text: String) : Call<TrackResponse>

}