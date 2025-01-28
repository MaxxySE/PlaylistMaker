package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.sharing.data.network.TrackApi
import com.example.playlistmaker.sharing.data.network.RetrofitNetworkClient
import com.example.playlistmaker.sharing.data.NetworkClient
import com.example.playlistmaker.sharing.data.settings.ConstData
import com.example.playlistmaker.search.data.local.SearchHistorySource
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            ConstData.getPlaylistPref(),
            Context.MODE_PRIVATE
        )
    }

    factory { Gson() }

    single {
        Retrofit.Builder()
            .baseUrl(ConstData.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<TrackApi> {
        get<Retrofit>().create(TrackApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(trackApi = get())
    }

    factory {
        SearchHistorySource(
            sharedPreferences = get(),
            gson = get()
        )
    }
}
