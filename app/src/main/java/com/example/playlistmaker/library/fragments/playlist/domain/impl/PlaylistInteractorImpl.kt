    package com.example.playlistmaker.library.fragments.playlist.domain.impl

    import android.net.Uri
    import com.example.playlistmaker.library.fragments.playlist.domain.api.PlaylistInteractor
    import com.example.playlistmaker.library.fragments.playlist.domain.api.PlaylistRepository
    import com.example.playlistmaker.library.fragments.playlist.domain.model.Playlist
    import com.example.playlistmaker.sharing.domain.models.Track
    import kotlinx.coroutines.flow.Flow

    class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {
        override suspend fun createPlaylist(name: String, description: String?, imageUri: Uri?) {
            repository.createPlaylist(name, description, imageUri)
        }
        override fun getPlaylists(): Flow<List<Playlist>> {
            return repository.getPlaylists()
        }
        override suspend fun addTrackToPlaylist(track: Track, playlistId: Int): Boolean {
            return repository.addTrackToPlaylist(track, playlistId)
        }
    }