package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsViewModel(
    private val interactor: SettingsInteractor
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> get() = _themeState

    init {
        _themeState.value = interactor.getThemeState()
    }

    fun onThemeSwitchChanged(enabled: Boolean) {
        interactor.setThemeState(enabled)
        _themeState.value = enabled
    }
}