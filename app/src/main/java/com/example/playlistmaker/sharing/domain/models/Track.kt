package com.example.playlistmaker.sharing.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?,
    var isFavorite: Boolean = false
) : Parcelable {

    fun getCoverArtwork(): String = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    fun getTrackTime(): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    fun getYear(): String? = releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4)
}