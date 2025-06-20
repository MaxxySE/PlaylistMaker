package com.example.playlistmaker.library.fragments.playlist.domain.api

import android.net.Uri
import com.example.playlistmaker.library.fragments.playlist.domain.model.Playlist
import com.example.playlistmaker.library.fragments.playlist.domain.model.PlaylistDetails
import com.example.playlistmaker.sharing.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(name: String, description: String?, imageUri: Uri?)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Int): Boolean
    fun getPlaylistDetails(playlistId: Int): Flow<PlaylistDetails>
    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int)
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun updatePlaylist(playlistId: Int, name: String, description: String?, imageUri: Uri?)
}