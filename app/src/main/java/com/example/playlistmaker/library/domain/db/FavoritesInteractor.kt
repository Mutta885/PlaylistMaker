package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    suspend fun insertNewFavorite(track: Track)
    suspend fun deleteFromFavorites(track:Track)
    suspend fun getFavorites(): Flow<List<Track>>
    suspend fun isFavorite(trackId: Long): Boolean

}

