package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.local.SearchHistorySource
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitInit
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TrackApi
import com.example.playlistmaker.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.data.settings.ConstData
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.HistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

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

    private fun getHistoryRepository(): HistoryRepository {
        val prefs = appContext.getSharedPreferences(
            ConstData().getPlaylistPref(),
            Context.MODE_PRIVATE
        )
        val dataSource = SearchHistorySource(prefs)
        return HistoryRepositoryImpl(dataSource)
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }
}