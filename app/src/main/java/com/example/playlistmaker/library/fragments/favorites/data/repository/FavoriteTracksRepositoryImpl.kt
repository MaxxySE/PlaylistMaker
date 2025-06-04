    package com.example.playlistmaker.library.fragments.favorites.data.repository

    import com.example.playlistmaker.library.fragments.favorites.data.db.AppDatabase
    import com.example.playlistmaker.library.fragments.favorites.data.db.converter.FavoritesTrackConverter
    import com.example.playlistmaker.library.fragments.favorites.domain.api.FavoriteTracksRepository
    import com.example.playlistmaker.sharing.domain.models.Track
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.map

    class FavoriteTracksRepositoryImpl(
        private val appDatabase: AppDatabase,
        private val converter: FavoritesTrackConverter
    ) : FavoriteTracksRepository {
        override suspend fun addTrack(track: Track) = appDatabase.favoriteTracksDao().addTrack(converter.map(track))
        override suspend fun deleteTrack(track: Track) = appDatabase.favoriteTracksDao().deleteTrack(converter.map(track))
        override fun getFavoriteTracks(): Flow<List<Track>> = appDatabase.favoriteTracksDao().getFavoriteTracks().map { list -> list.map { entity -> converter.map(entity) } }
        override suspend fun getFavoriteTrackIds(): List<Int> = appDatabase.favoriteTracksDao().getFavoriteTrackIds()
    }
