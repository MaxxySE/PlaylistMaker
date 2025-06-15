package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.fragments.favorites.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.library.fragments.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.fragments.playlist.domain.model.Playlist
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.sharing.domain.models.Track
import com.example.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: PlayerInteractor,
    private val favoritesInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val track: Track
) : ViewModel() {
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _addToPlaylistState = SingleLiveEvent<AddToPlaylistState>()
    val addToPlaylistState: LiveData<AddToPlaylistState> = _addToPlaylistState


    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private var timerJob: Job? = null

    init {
        _isFavorite.value = track.isFavorite

        viewModelScope.launch {
            val actualIsFavorite = favoritesInteractor.isTrackFavorite(track.trackId)
            track.isFavorite = actualIsFavorite
            _isFavorite.postValue(actualIsFavorite)
        }


        interactor.setPlayerStateListener { state ->
            _playerState.postValue(state)
            when (state) {
                is PlayerState.Playing -> startPlayerTimer()
                is PlayerState.Paused, is PlayerState.Prepared, is PlayerState.Completed -> timerJob?.cancel()
                else -> { }
            }
        }

        if (!track.previewUrl.isNullOrEmpty()) {
            prepare(track.previewUrl!!)
        } else {
            _playerState.postValue(PlayerState.Idle)
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val currentFavoriteStatus = track.isFavorite
            if (currentFavoriteStatus) {
                favoritesInteractor.deleteTrack(track)
            } else {
                favoritesInteractor.addTrack(track)
            }
            track.isFavorite = !currentFavoriteStatus
            _isFavorite.postValue(track.isFavorite)
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { receivedPlaylists ->
                    _playlists.postValue(receivedPlaylists)
                }
        }
    }

    fun onPlaylistSelected(playlist: Playlist) {
        viewModelScope.launch {
            val wasAdded = playlistInteractor.addTrackToPlaylist(track, playlist.id)
            if (wasAdded) {
                _addToPlaylistState.postValue(AddToPlaylistState.Success(playlist.name))
            } else {
                _addToPlaylistState.postValue(AddToPlaylistState.AlreadyExists(playlist.name))
            }
        }
    }

    private fun startPlayerTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(TIMER_DELAY_MS)
                val currentPosition = interactor.getCurrentPosition()
                _playerState.postValue(PlayerState.PositionUpdate(currentPosition))
            }
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        interactor.stop()
    }

    companion object {
        private const val TIMER_DELAY_MS = 300L
    }
}

sealed class AddToPlaylistState {
    data class Success(val playlistName: String) : AddToPlaylistState()
    data class AlreadyExists(val playlistName: String) : AddToPlaylistState()
}