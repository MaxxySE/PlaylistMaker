package com.example.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.library.fragments.playlist.activities.CreationActivity
import com.example.playlistmaker.player.ui.adapter.PlaylistBottomSheetAdapter
import com.example.playlistmaker.player.ui.viewmodel.AddToPlaylistState
import com.example.playlistmaker.player.ui.viewmodel.PlayerState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.sharing.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val playlistAdapter = PlaylistBottomSheetAdapter { playlist ->
        viewModel.onPlaylistSelected(playlist)
    }

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getTrack()

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomSheet()
        setPlayerDetails()
        setupListeners()
        observeViewModel()

        binding.playBtn.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            viewModel.loadPlaylists()
        }
    }

    private fun setupBottomSheet() {
        binding.playlistsBsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.playlistsBsRecyclerView.adapter = playlistAdapter

        val bottomSheetContainer = binding.playlistsBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)


        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility = if (newState != BottomSheetBehavior.STATE_HIDDEN) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1) / 2
            }
        })
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
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.dp8)))
            .into(binding.playerCover)

        binding.playerName.text = track.trackName
        binding.playerArtist.text = track.artistName
        binding.trackLength.text = track.getTrackTime()
        binding.trackAlbum.text = track.collectionName
        binding.trackYear.text = track.getYear()
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country

        binding.albumFrame.visibility = if (track.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.playBtn.setOnClickListener { viewModel.play() }
        binding.pauseBtn.setOnClickListener { viewModel.pause() }
        binding.likeBtn.setOnClickListener { viewModel.onFavoriteClicked() }

        binding.addPlaylistBtn.setOnClickListener {
            viewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistButton.setOnClickListener {
            val intent = Intent(this, CreationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(this) { state ->
            when (state) {
                is PlayerState.Playing -> {
                    binding.playBtn.visibility = View.INVISIBLE
                    binding.pauseBtn.visibility = View.VISIBLE
                }
                is PlayerState.Paused, is PlayerState.Prepared, is PlayerState.Completed -> {
                    binding.pauseBtn.visibility = View.INVISIBLE
                    binding.playBtn.visibility = View.VISIBLE
                }
                is PlayerState.Idle -> {
                    binding.playBtn.isEnabled = false
                }
                is PlayerState.Error -> {
                    Log.d("PSError", state.message)
                }
                is PlayerState.PositionUpdate -> {
                    updateTimer(state.position)
                }
            }
            if (state is PlayerState.Prepared) binding.playBtn.isEnabled = true
            if (state is PlayerState.Completed) binding.playerTimer.text = "00:00"
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            binding.likeBtn.isSelected = isFavorite
        }

        viewModel.playlists.observe(this) { playlists ->
            playlistAdapter.playlists = playlists.toMutableList()
            playlistAdapter.notifyDataSetChanged()
        }

        viewModel.addToPlaylistState.observe(this) { state ->
            when (state) {
                is AddToPlaylistState.Success -> {
                    Toast.makeText(this, "Добавлено в плейлист ${state.playlistName}", Toast.LENGTH_SHORT).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                is AddToPlaylistState.AlreadyExists -> {
                    Toast.makeText(this, "Трек уже добавлен в плейлист ${state.playlistName}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTimer(position: Long) {
        binding.playerTimer.text = formatTime(position)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}