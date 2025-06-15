package com.example.playlistmaker.library.fragments.playlist.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String?,
    val imageUri: String?,
    val trackIds: String,
    val trackCount: Int
)