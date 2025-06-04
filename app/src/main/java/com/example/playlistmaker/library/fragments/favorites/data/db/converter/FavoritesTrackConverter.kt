package com.example.playlistmaker.library.fragments.favorites.data.db.converter

import com.example.playlistmaker.library.fragments.favorites.data.db.entity.TrackDbEntity
import com.example.playlistmaker.sharing.domain.models.Track

class FavoritesTrackConverter {
    fun map(track: Track): TrackDbEntity {
        return TrackDbEntity(track.trackId, track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, track.previewUrl, System.currentTimeMillis())
    }

    fun map(entity: TrackDbEntity): Track {
        return Track(entity.trackId, entity.trackName, entity.artistName, entity.trackTimeMillis, entity.artworkUrl100, entity.collectionName, entity.releaseDate, entity.primaryGenreName, entity.country, entity.previewUrl, isFavorite = true)
    }
}