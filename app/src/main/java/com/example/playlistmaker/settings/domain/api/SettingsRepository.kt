package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun getThemeState(): Boolean
    fun setThemeState(enabled: Boolean)
}