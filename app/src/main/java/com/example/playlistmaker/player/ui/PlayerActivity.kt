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
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.sharing.domain.models.Track
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getTrack()

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPlayerDetails()
        setupListeners()
        observeViewModel()

        binding.playBtn.isEnabled = false
    }

    private fun getTrack() {
        track = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("track")
        } ?: throw IllegalArgumentException("Track data missing")

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

        binding.likeBtn.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(this, Observer { state ->
            when (state) {
                is PlayerState.Idle -> {
                    binding.playBtn.isEnabled = false
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
                    Log.d("PSError", state.message)
                }
                is PlayerState.PositionUpdate -> {
                    updateTimer(state.position)
                }
            }
        })

        viewModel.isFavorite.observe(this) { isFavorite ->
            binding.likeBtn.isSelected = isFavorite
        }

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

}