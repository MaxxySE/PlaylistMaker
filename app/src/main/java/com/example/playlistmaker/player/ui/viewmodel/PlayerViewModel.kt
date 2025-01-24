package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerState
import com.example.playlistmaker.player.domain.api.PlayerInteractor

class PlayerViewModel(
    private val interactor: PlayerInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    init {
        interactor.setPlayerStateListener { state ->
            _playerState.postValue(state)
        }
    }

    fun prepare(url: String) {
        interactor.prepare(url)
    }

    fun play() {
        interactor.play()
    }

    fun pause() {
        interactor.pause()
    }

    fun stop() {
        interactor.stop()
    }

    fun updateCurrentPosition() {
        val currentPosition = interactor.getCurrentPosition()
        _playerState.postValue(PlayerState.PositionUpdate(currentPosition))
    }

    override fun onCleared() {
        super.onCleared()
        interactor.stop()
    }
}