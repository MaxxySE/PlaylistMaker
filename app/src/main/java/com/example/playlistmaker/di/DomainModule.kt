package com.example.playlistmaker.di

import com.example.playlistmaker.sharing.domain.api.TrackInteractor
import com.example.playlistmaker.sharing.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    single<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }

    single<HistoryInteractor> {
        HistoryInteractorImpl(repository = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }

    single<PlayerInteractor> {
        PlayerInteractorImpl(repository = get())
    }
}
