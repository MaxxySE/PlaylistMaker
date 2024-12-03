package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.additional.ConstData


class App : Application() {

    private val constData = ConstData()
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val playlistPrefs = getSharedPreferences(constData.getPlaylistPref(), MODE_PRIVATE)

        darkTheme = playlistPrefs.getBoolean(constData.getThemeSwitchKey(), false)

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}