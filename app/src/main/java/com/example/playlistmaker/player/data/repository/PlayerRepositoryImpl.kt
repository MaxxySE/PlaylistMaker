package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.ui.viewmodel.PlayerState
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private var mediaPlayer: MediaPlayer? = null
    private var listener: ((PlayerState) -> Unit)? = null

    override fun prepare(url: String) {
        releasePlayer()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                listener?.invoke(PlayerState.Prepared)
            }
            setOnCompletionListener {
                listener?.invoke(PlayerState.Completed)
            }
            setOnErrorListener { _, _, _ ->
                listener?.invoke(PlayerState.Error("Playback error"))
                true
            }
            prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer?.start()
        listener?.invoke(PlayerState.Playing)
    }

    override fun pause() {
        mediaPlayer?.pause()
        listener?.invoke(PlayerState.Paused)
    }

    override fun stop() {
        mediaPlayer?.stop()
        releasePlayer()
        listener?.invoke(PlayerState.Idle)
    }

    override fun getCurrentState(): PlayerState {
        return when {
            mediaPlayer == null -> PlayerState.Idle
            mediaPlayer!!.isPlaying -> PlayerState.Playing
            else -> PlayerState.Paused
        }
    }

    override fun setPlayerStateListener(listener: (PlayerState) -> Unit) {
        this.listener = listener
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}