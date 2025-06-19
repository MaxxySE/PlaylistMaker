package com.example.playlistmaker.library.fragments.playlist.fragments.editplaylist.viewmodels

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.fragments.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.fragments.playlist.domain.model.Playlist
import com.example.playlistmaker.library.fragments.playlist.fragments.creation.viewmodels.CreationViewModel
import kotlinx.coroutines.launch
import java.io.File

class PlaylistEditViewModel(playlistInteractor: PlaylistInteractor) : CreationViewModel(playlistInteractor) {

    private var playlistId: Int = -1

    fun init(playlist: Playlist) {
        this.playlistId = playlist.id
        if (playlist.imageUri != null) {
            val imageFile = File(playlist.imageUri)
            onImageSelected(imageFile.toUri())
        }
    }

    fun updatePlaylist(name: String, description: String?) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlistId, name, description, imageUri.value)
            _playlistCreated.postValue(name)
        }
    }
}