package com.example.playlistmaker.di

import com.example.playlistmaker.library.fragments.playlist.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.library.fragments.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.sharing.domain.api.TrackRepository
import com.example.playlistmaker.sharing.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import org.koin.dsl.module
import com.example.playlistmaker.library.fragments.favorites.data.repository.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.library.fragments.favorites.domain.api.FavoriteTracksRepository


val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(networkClient = get(), favoriteTracksRepository = get())
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

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(appDatabase = get(), converter = get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            appDatabase = get(),
            imageStorageManager = get(),
            gson = get(),
            playlistTrackDao = get()
        )
    }
}
