package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.ui.viewmodel.PlayerState

class PlayerRepositoryImpl : PlayerRepository {

    private var mediaPlayer: MediaPlayer? = null
    private var listener: ((PlayerState) -> Unit)? = null

    override fun prepare(url: String) {
        releasePlayer()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()

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
        }
    }

    override fun play() {
        mediaPlayer?.start()
        listener?.invoke(PlayerState.Playing)
    }

    override fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            listener?.invoke(PlayerState.Paused)
        }
    }

    override fun stop() {
        releasePlayer()
    }

    override fun getCurrentState(): PlayerState {
        TODO("Not yet implemented")
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