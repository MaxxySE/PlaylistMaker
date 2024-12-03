package com.example.playlistmaker.entities

import android.icu.text.SimpleDateFormat
import java.io.Serializable
import java.util.Locale

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
) : Serializable{
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    fun getTrackTime(): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis.toLong())

    fun getYear() : String {
        return releaseDate?.replaceAfter('-',"")?.replace("-", "") ?: releaseDate
    }
}
