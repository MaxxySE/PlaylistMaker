package com.example.playlistmaker.library.fragments.favorites.data.db.dao

import androidx.room.*
import com.example.playlistmaker.library.fragments.favorites.data.db.entity.TrackDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTracksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(track: TrackDbEntity)

    @Delete
    suspend fun deleteTrack(trackEntity: TrackDbEntity)

    @Query("SELECT * FROM favorite_tracks_table ORDER BY dateAdded DESC")
    fun getFavoriteTracks(): Flow<List<TrackDbEntity>>

    @Query("SELECT trackId FROM favorite_tracks_table")
    suspend fun getFavoriteTrackIds(): List<Int>
}