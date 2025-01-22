package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {
    override fun getThemeState(): Boolean = repository.getThemeState()
    override fun setThemeState(enabled: Boolean) = repository.setThemeState(enabled)
}