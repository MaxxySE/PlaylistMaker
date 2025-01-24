package com.example.playlistmaker.player.ui.viewmodel

sealed class PlayerState {
    object Idle : PlayerState()
    object Prepared : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Completed : PlayerState()
    data class Error(val message: String) : PlayerState()
    data class PositionUpdate(val position: Long) : PlayerState()
}