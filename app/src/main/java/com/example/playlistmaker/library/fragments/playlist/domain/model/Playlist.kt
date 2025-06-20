package com.example.playlistmaker.library.fragments.playlist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Int,
    val name: String,
    val description: String?,
    val imageUri: String?,
    val trackCount: Int
) : Parcelable