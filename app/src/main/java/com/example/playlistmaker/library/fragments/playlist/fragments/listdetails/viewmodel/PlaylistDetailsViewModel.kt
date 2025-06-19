package com.example.playlistmaker.library.fragments.playlist.fragments.listdetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.fragments.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.fragments.playlist.domain.model.PlaylistDetails
import com.example.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlistId: Int = savedStateHandle.get<Int>("playlistId")!!

    private val _screenState = MutableLiveData<PlaylistDetailsScreenState>()
    val screenState: LiveData<PlaylistDetailsScreenState> = _screenState

    private val _closeScreenEvent = SingleLiveEvent<Unit>()
    val closeScreenEvent: LiveData<Unit> = _closeScreenEvent


    private val _sharePlaylistEvent = SingleLiveEvent<String?>()
    val sharePlaylistEvent: LiveData<String?> = _sharePlaylistEvent

    init {
        val playlistId = savedStateHandle.get<Int>("playlistId")!!
        loadPlaylistDetails(playlistId)
    }

    private fun loadPlaylistDetails(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistDetails(playlistId).collect { details ->
                _screenState.postValue(PlaylistDetailsScreenState.Content(details))
            }
        }
    }

    fun deleteTrack(track: com.example.playlistmaker.sharing.domain.models.Track) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(track.trackId, playlistId)
        }
    }

    fun onSharePlaylistClicked() {
        val currentState = _screenState.value
        if (currentState is PlaylistDetailsScreenState.Content) {
            val details = currentState.details
            if (details.tracks.isEmpty()) {
                _sharePlaylistEvent.postValue(null)
                return
            }
            _sharePlaylistEvent.postValue(formatPlaylistForSharing(details))
        }
    }

    private fun formatPlaylistForSharing(details: PlaylistDetails): String {
        val sb = StringBuilder()
        sb.append(details.playlist.name).append("\n")
        if (!details.playlist.description.isNullOrEmpty()) {
            sb.append(details.playlist.description).append("\n")
        }
        val trackCount = details.playlist.trackCount
        sb.append(
            "$trackCount трек/трека/треков"
        ).append("\n\n")

        details.tracks.forEachIndexed { index, track ->
            val trackDuration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            sb.append("${index + 1}. ${track.artistName} - ${track.trackName} ($trackDuration)").append("\n")
        }
        return sb.toString()
    }

    fun onDeletePlaylistConfirmed() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
            _closeScreenEvent.postValue(Unit)
        }
    }
}

sealed interface PlaylistDetailsScreenState {
    data class Content(val details: PlaylistDetails) : PlaylistDetailsScreenState
}