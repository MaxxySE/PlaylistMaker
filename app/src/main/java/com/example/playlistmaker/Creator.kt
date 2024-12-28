package com.example.playlistmaker

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitInit
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TrackApi
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getNetworkClient(): NetworkClient {
        val retrofitInit = RetrofitInit()
        val retrofit = retrofitInit.getRetrofit("https://itunes.apple.com")
        val trackApi = retrofit.create(TrackApi::class.java)
        return RetrofitNetworkClient(trackApi)
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(getNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }
}