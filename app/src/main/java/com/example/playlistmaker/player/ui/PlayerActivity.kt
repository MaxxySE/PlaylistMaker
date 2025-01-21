package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.sharing.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track
    private lateinit var previewUrl: String

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition.toLong())
                binding.playerTimer.text = currentTime
                handler.postDelayed(this, TIMER_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initElements()
        setPlayer()
        listeners()

        binding.playBtn.isEnabled = false

        if (track.previewUrl?.isNotEmpty() == true) {
            preparePlayer()
        } else {
            Toast.makeText(this, "Song Preview Unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initElements() {
        track = intent.getParcelableExtra("track") ?: throw IllegalArgumentException("Track data missing")
    }

    private fun setPlayer() {
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

        previewUrl = track.previewUrl.toString()
    }

    private fun listeners() {
        binding.backBtn.setOnClickListener {
            this.finish()
        }

        binding.playBtn.setOnClickListener {
            startPlayer()
        }

        binding.pauseBtn.setOnClickListener {
            pausePlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.playBtn.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            stopTimer()
            binding.playerTimer.text = "00:00"
            binding.pauseBtn.visibility = View.INVISIBLE
            binding.playBtn.visibility = View.VISIBLE
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playBtn.visibility = View.INVISIBLE
        binding.pauseBtn.visibility = View.VISIBLE
        playerState = STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            stopTimer()
            binding.pauseBtn.visibility = View.INVISIBLE
            binding.playBtn.visibility = View.VISIBLE
            playerState = STATE_PAUSED
        }
    }

    private fun stopTimer() {
        handler.removeCallbacks(updateRunnable)
    }

    private fun startTimer() {
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_DELAY = 250L
    }
}
