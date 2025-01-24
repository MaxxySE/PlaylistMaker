// creator/Creator.kt
package com.example.playlistmaker.creator

import android.app.Application
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
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModelFactory
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModelFactory

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

    private fun provideTrackInteractor(): TrackInteractor {
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

    private fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        val prefs = appContext.getSharedPreferences(
            ConstData().getPlaylistPref(),
            Context.MODE_PRIVATE
        )
        return SettingsRepositoryImpl(prefs)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    private fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    fun provideSearchViewModelFactory(): SearchViewModelFactory {
        val trackInteractor = provideTrackInteractor()
        val historyInteractor = provideHistoryInteractor()
        return SearchViewModelFactory(appContext as Application, trackInteractor, historyInteractor)
    }

    fun providePlayerViewModelFactory(): PlayerViewModelFactory {
        val interactor = providePlayerInteractor()
        return PlayerViewModelFactory(interactor)
    }
}
