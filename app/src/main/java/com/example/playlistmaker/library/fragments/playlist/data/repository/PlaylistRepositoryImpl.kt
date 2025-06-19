package com.example.playlistmaker.library.fragments.playlist.data.repository

import android.net.Uri
import com.example.playlistmaker.AppDatabase
import com.example.playlistmaker.library.fragments.playlist.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.library.fragments.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.fragments.playlist.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.library.fragments.playlist.data.local.ImageStorageManager
import com.example.playlistmaker.library.fragments.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.library.fragments.playlist.domain.model.Playlist
import com.example.playlistmaker.library.fragments.playlist.domain.model.PlaylistDetails
import com.example.playlistmaker.sharing.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

    override fun getPlaylistDetails(playlistId: Int): Flow<PlaylistDetails> {
        return appDatabase.playlistDao().getPlaylistFlowById(playlistId)
            .filterNotNull()
            .flatMapLatest { playlistEntity ->
                val trackIds = gson.fromJson(playlistEntity.trackIds, Array<Int>::class.java)
                if (trackIds.isEmpty()) {
                    flowOf(PlaylistDetails(playlist = mapEntityToDomain(playlistEntity), tracks = emptyList()))
                } else {
                    appDatabase.playlistTrackDao().getTracksByIds(trackIds.toList()).map { trackEntities ->
                        PlaylistDetails(
                            playlist = mapEntityToDomain(playlistEntity),
                            tracks = trackEntities.map { mapTrackEntityToDomain(it) }
                        )
                    }
                }
            }
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int) {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
        val trackIds = gson.fromJson(playlist.trackIds, Array<Int>::class.java).toMutableList()
        trackIds.remove(trackId)
        val updatedPlaylist = playlist.copy(
            trackIds = gson.toJson(trackIds),
            trackCount = trackIds.size
        )
        appDatabase.playlistDao().updatePlaylist(updatedPlaylist)

        checkAndDeleteOrphanTrack(trackId)
    }

    private suspend fun checkAndDeleteOrphanTrack(trackId: Int) {
        val allPlaylists = appDatabase.playlistDao().getPlaylists().first()

        val isTrackNeeded = allPlaylists.any {
            gson.fromJson(it.trackIds, Array<Int>::class.java).contains(trackId)
        }

        if (!isTrackNeeded) {
            playlistTrackDao.deleteTrackById(trackId)
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

    override suspend fun deletePlaylist(playlistId: Int) {
        val playlistToDelete = appDatabase.playlistDao().getPlaylistById(playlistId)
        val trackIds = gson.fromJson(playlistToDelete.trackIds, Array<Int>::class.java)

        appDatabase.playlistDao().deletePlaylistById(playlistId)

        trackIds.forEach { trackId ->
            checkAndDeleteOrphanTrack(trackId)
        }
    }

    override suspend fun updatePlaylist(playlistId: Int, name: String, description: String?, imageUri: Uri?) {
        var coverPath = appDatabase.playlistDao().getPlaylistById(playlistId).imageUri
        if (imageUri != null) {
            coverPath = imageStorageManager.saveImage(imageUri)
        }

        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)

        val updatedPlaylist = playlist.copy(
            name = name,
            description = description,
            imageUri = coverPath
        )
        appDatabase.playlistDao().updatePlaylist(updatedPlaylist)
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

    private fun mapTrackEntityToDomain(entity: PlaylistTrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }
}