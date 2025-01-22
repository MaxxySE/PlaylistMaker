package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.ui.viewmodel.PlayerState
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    override fun prepare(url: String) {
        repository.prepare(url)
    }

    override fun play() {
        repository.play()
    }

    override fun pause() {
        repository.pause()
    }

    override fun stop() {
        repository.stop()
    }

    override fun getCurrentState(): PlayerState {
        return repository.getCurrentState()
    }

    override fun setPlayerStateListener(listener: (PlayerState) -> Unit) {
        repository.setPlayerStateListener(listener)
    }

    override fun getCurrentPosition(): Long {
        return repository.getCurrentPosition()
    }
}