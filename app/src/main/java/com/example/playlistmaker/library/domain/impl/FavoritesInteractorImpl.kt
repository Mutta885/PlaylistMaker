package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.domain.db.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) : FavoritesInteractor{

    override suspend fun insertNewFavorite(track: Track) {
        favoritesRepository.insertNewFavorite(track)
    }


    override suspend fun deleteFromFavorites(track:Track) {
        favoritesRepository.deleteFromFavorites(track)
    }

    override suspend fun getFavorites(): Flow<List<Track>> {
        return favoritesRepository.getFavorites()
    }


    override suspend fun isFavorite(trackId: Long): Boolean {
        return favoritesRepository.isFavorite(trackId)
    }

}