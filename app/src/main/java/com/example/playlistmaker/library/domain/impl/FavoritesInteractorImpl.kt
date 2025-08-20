package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.domain.db.FavoritesRepository
import com.example.playlistmaker.library.domain.models.TrackWithOrder
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


class FavoritesInteractorImpl(private val repository: FavoritesRepository) :
    FavoritesInteractor {
    override suspend fun changeFavorites(track: Track) {
        repository.changeFavorites(track)
    }


    override suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean {
        return repository.checkIfTrackIsFavorite(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override fun getTracksByIds(tracks: List<TrackWithOrder>): Flow<List<Track>> {
        return repository.getTracksByIds(tracks)
    }
}