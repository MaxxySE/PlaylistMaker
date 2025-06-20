package com.example.playlistmaker.library.fragments.playlist.fragments.creation.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.fragments.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.launch

open class CreationViewModel(
    val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    val _playlistCreated = SingleLiveEvent<String>()
    val playlistCreated: LiveData<String> = _playlistCreated

    fun onImageSelected(uri: Uri) {
        _imageUri.postValue(uri)
    }

    fun createPlaylist(name: String, description: String?) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(name, description, _imageUri.value)
            _playlistCreated.postValue(name)
        }
    }
}