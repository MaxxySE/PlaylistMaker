package com.example.playlistmaker.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String? = null,
) : Parcelable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    fun getTrackTime(): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis.toLong())

    fun getYear(): String {
        return releaseDate.replaceAfter('-', "").replace("-", "")
    }
}