    package com.example.playlistmaker

    import androidx.room.Database
    import androidx.room.RoomDatabase
    import com.example.playlistmaker.library.fragments.playlist.data.db.dao.PlaylistDao
    import com.example.playlistmaker.library.fragments.playlist.data.db.entity.PlaylistEntity
    import com.example.playlistmaker.library.fragments.favorites.data.db.dao.FavoriteTracksDao
    import com.example.playlistmaker.library.fragments.favorites.data.db.entity.TrackDbEntity
    import com.example.playlistmaker.library.fragments.playlist.data.db.dao.PlaylistTrackDao
    import com.example.playlistmaker.library.fragments.playlist.data.db.entity.PlaylistTrackEntity

    @Database(version = 3, entities = [TrackDbEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class])
    abstract class AppDatabase : RoomDatabase() {
        abstract fun favoriteTracksDao(): FavoriteTracksDao

        abstract fun playlistDao(): PlaylistDao

        abstract fun playlistTrackDao(): PlaylistTrackDao
    }