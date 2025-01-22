package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun getThemeState(): Boolean
    fun setThemeState(enabled: Boolean)
}