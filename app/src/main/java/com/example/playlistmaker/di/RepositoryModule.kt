package com.example.playlistmaker.di

import com.example.playlistmaker.sharing.domain.api.TrackRepository
import com.example.playlistmaker.sharing.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(networkClient = get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(searchHistoryDataSource = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPreferences = get())
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl()
    }
}
