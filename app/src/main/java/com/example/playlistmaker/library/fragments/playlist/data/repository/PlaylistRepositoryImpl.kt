package com.example.playlistmaker.library.fragments.playlist.data.repository

import android.net.Uri
import com.example.playlistmaker.AppDatabase
import com.example.playlistmaker.library.fragments.playlist.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.library.fragments.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.fragments.playlist.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.library.fragments.playlist.data.local.ImageStorageManager
import com.example.playlistmaker.library.fragments.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.library.fragments.playlist.domain.model.Playlist
import com.example.playlistmaker.sharing.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val imageStorageManager: ImageStorageManager,
    private val gson: Gson,
    private val playlistTrackDao: PlaylistTrackDao
) : PlaylistRepository {

    override suspend fun createPlaylist(name: String, description: String?, imageUri: Uri?) {
        val savedImagePath = if (imageUri != null) {
            imageStorageManager.saveImage(imageUri)
        } else {
            null
        }

        val playlistEntity = PlaylistEntity(
            name = name,
            description = description,
            imageUri = savedImagePath,
            trackIds = gson.toJson(emptyList<Int>()),
            trackCount = 0
        )
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { playlists ->
            playlists.map { mapEntityToDomain(it) }
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Int): Boolean {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
        val trackIds = gson.fromJson(playlist.trackIds, Array<Int>::class.java).toMutableList()

        if (trackIds.contains(track.trackId)) {
            return false
        }

        playlistTrackDao.insertTrack(mapTrackToPlaylistTrackEntity(track))

        trackIds.add(track.trackId)
        val updatedPlaylist = playlist.copy(
            trackIds = gson.toJson(trackIds),
            trackCount = playlist.trackCount + 1
        )
        appDatabase.playlistDao().updatePlaylist(updatedPlaylist)

        return true
    }

    private fun mapTrackToPlaylistTrackEntity(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    private fun mapEntityToDomain(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            imageUri = entity.imageUri,
            trackCount = entity.trackCount
        )
    }
}