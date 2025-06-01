package com.example.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.viewmodel.PlayerState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.sharing.data.dto.TrackDto
import com.example.playlistmaker.sharing.data.dto.toDomain
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.sharing.domain.models.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initElements()
        setPlayerDetails()
        setupListeners()
        observeViewModel()

        binding.playBtn.isEnabled = false

        if (!track.previewUrl.isNullOrEmpty()) {
            viewModel.prepare(track.previewUrl!!)
        }
    }

    private fun initElements() {
        val trackDto = intent.getParcelableExtra<TrackDto>("track")
            ?: throw IllegalArgumentException("Track data missing")
        track = trackDto.toDomain()
    }

    private fun setPlayerDetails() {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.album_placeholder)
            .fitCenter()
            .transform(RoundedCorners(20))
            .into(binding.playerCover)

        binding.playerName.text = track.trackName
        binding.playerArtist.text = track.artistName
        binding.trackLength.text = track.getTrackTime()

        binding.trackAlbum.text = track.collectionName
        binding.albumFrame.visibility = View.VISIBLE

        binding.trackYear.text = track.getYear()
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.playBtn.setOnClickListener {
            viewModel.play()
        }

        binding.pauseBtn.setOnClickListener {
            viewModel.pause()
        }
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(this, Observer { state ->
            // Упрощенный when, он только меняет UI
            when (state) {
                is PlayerState.Idle -> {
                    // Это состояние может не приходить, но на всякий случай
                    binding.playBtn.isEnabled = false // Плеер еще не готов
                    binding.pauseBtn.visibility = View.INVISIBLE
                    binding.playBtn.visibility = View.VISIBLE
                    binding.playerTimer.text = "00:00"
                }
                is PlayerState.Prepared -> {
                    binding.playBtn.isEnabled = true
                    binding.pauseBtn.visibility = View.INVISIBLE
                    binding.playBtn.visibility = View.VISIBLE
                    binding.playerTimer.text = "00:00"
                }
                is PlayerState.Playing -> {
                    binding.playBtn.visibility = View.INVISIBLE
                    binding.pauseBtn.visibility = View.VISIBLE
                }
                is PlayerState.Paused -> {
                    binding.pauseBtn.visibility = View.INVISIBLE
                    binding.playBtn.visibility = View.VISIBLE
                }
                is PlayerState.Completed -> {
                    binding.playerTimer.text = "00:00"
                    binding.pauseBtn.visibility = View.INVISIBLE
                    binding.playBtn.visibility = View.VISIBLE
                }
                is PlayerState.Error -> {
                    // Обработка ошибки
                    Log.d("PSError", state.message)
                }
                is PlayerState.PositionUpdate -> {
                    // Просто обновляем таймер
                    updateTimer(state.position)
                }
            }
        })
    }


    private fun updateTimer(position: Long) {
        binding.playerTimer.text = formatTime(position)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    companion object {
        private const val TIMER_DELAY = 250L
    }
}