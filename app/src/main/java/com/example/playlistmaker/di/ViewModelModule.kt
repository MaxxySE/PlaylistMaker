package com.example.playlistmaker.di

import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SettingsViewModel(interactor = get())
    }

    viewModel {
        SearchViewModel(
            application = androidApplication(),
            trackInteractor = get(),
            historyInteractor = get()
        )
    }

    viewModel {
        PlayerViewModel(interactor = get())
    }
}
