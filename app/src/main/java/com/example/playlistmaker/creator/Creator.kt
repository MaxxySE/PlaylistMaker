package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.sharing.data.NetworkClient
import com.example.playlistmaker.search.data.local.SearchHistorySource
import com.example.playlistmaker.sharing.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.sharing.data.network.RetrofitInit
import com.example.playlistmaker.sharing.data.network.RetrofitNetworkClient
import com.example.playlistmaker.sharing.data.network.TrackApi
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.sharing.data.settings.ConstData
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.sharing.domain.api.TrackInteractor
import com.example.playlistmaker.sharing.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.sharing.domain.impl.TrackInteractorImpl

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