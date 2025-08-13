package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.db.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.toTrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(private val appDatabase: AppDatabase, private val trackDbConvertor: TrackDbConvertor) : FavoritesRepository {

    override suspend fun insertNewFavorite(track: Track) {
        appDatabase.trackDao().insertNewFavorite(track.toTrackEntity())//(trackDbConvertor.map(track))
    }


    override suspend fun deleteFromFavorites(track: Track) {
        appDatabase.trackDao().deleteFromFavorites(track.toTrackEntity())
    }

    override suspend fun getFavorites(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }


    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertFromEntityTrack(tracks: List<Track>): List<TrackEntity> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return appDatabase.trackDao().getFavoriteId(trackId) != null
    }


}