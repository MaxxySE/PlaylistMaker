package com.example.playlistmaker.sharing.domain.models

import android.annotation.SuppressLint

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
    val previewUrl: String? = null
) {

    fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

    @SuppressLint("DefaultLocale")
    fun getTrackTime(): String {
        val millis = trackTimeMillis.toLongOrNull() ?: 0L
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getYear(): String {
        return releaseDate.substringOrNull(0, 4) ?: "Unknown"
    }
}

fun String.substringOrNull(startIndex: Int, endIndex: Int): String? {
    return try {
        this.substring(startIndex, endIndex)
    } catch (e: Exception) {
        null
    }
}