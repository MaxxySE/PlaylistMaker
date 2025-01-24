package com.example.playlistmaker.settings.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    private val THEME_SWITCH_KEY = "theme_switcher_key"

    override fun getThemeState(): Boolean {
        return sharedPreferences.getBoolean(THEME_SWITCH_KEY, false)
    }

    override fun setThemeState(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(THEME_SWITCH_KEY, enabled).apply()
    }
}