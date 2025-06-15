package com.example.playlistmaker.library.fragments.playlist.domain.api

import android.net.Uri
import com.example.playlistmaker.library.fragments.playlist.domain.model.Playlist
import com.example.playlistmaker.sharing.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String?, imageUri: Uri?)
    fun getPlaylists(): Flow<List<  Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Int): Boolean
}