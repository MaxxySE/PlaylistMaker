package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.ui.viewmodel.PlayerState

interface PlayerInteractor {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun stop()
    fun getCurrentState(): PlayerState
    fun setPlayerStateListener(listener: (PlayerState) -> Unit)
    fun getCurrentPosition(): Long
}