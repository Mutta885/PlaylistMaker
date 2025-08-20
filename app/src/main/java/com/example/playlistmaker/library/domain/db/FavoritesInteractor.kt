package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.library.domain.models.TrackWithOrder
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface FavoritesInteractor {
    suspend fun changeFavorites(track: Track)
    suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean
    fun getFavoriteTracks(): Flow<List<Track>>
    fun getTracksByIds(tracks: List<TrackWithOrder>): Flow<List<Track>>

}