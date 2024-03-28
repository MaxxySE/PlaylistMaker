package com.example.playlistmaker.entities

data class Track(
    var trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
)
