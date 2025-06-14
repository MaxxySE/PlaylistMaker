package com.example.playlistmaker.library.fragments.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.fragments.favorites.data.db.dao.FavoriteTracksDao
import com.example.playlistmaker.library.fragments.favorites.data.db.entity.TrackDbEntity

@Database(version = 1, entities = [TrackDbEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
}