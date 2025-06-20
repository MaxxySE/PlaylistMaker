package com.example.playlistmaker.library.fragments.playlist.domain.model

import com.example.playlistmaker.sharing.domain.models.Track

data class PlaylistDetails(
    val playlist: Playlist,
    val tracks: List<Track>
)