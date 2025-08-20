package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.db.FavoritesRepository
import com.example.playlistmaker.library.domain.models.TrackWithOrder
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FavoritesRepositoryImpl(private val appDatabase: AppDatabase) :
    FavoritesRepository {

    override suspend fun changeFavorites(track: Track) {
        appDatabase.trackDao().addTrack(track.toEntity(System.currentTimeMillis()))
    }

    override suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean {
        return appDatabase.trackDao().checkIfTrackIsFavorite(trackId) != null
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getFavoriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> track.toDomain() }
    }

    override fun getTracksByIds(tracks: List<TrackWithOrder>): Flow<List<Track>> = flow {
        val sortedTracksWithOrder = tracks.sortedByDescending { it.timestamp }
        sortedTracksWithOrder.forEach { track ->
        }

        val trackIds = sortedTracksWithOrder.map { it.trackId }

        val tracksFromDb = appDatabase.trackDao().getTracksByIds(trackIds)

        val trackIdToTimestamp = sortedTracksWithOrder.associateBy { it.trackId }

        val tracksWithTimestamp = tracksFromDb.map { track ->
            val timestamp = trackIdToTimestamp[track.trackId]?.timestamp
            track.copy(timestamp = timestamp ?: 0L)  // Если timestamp отсутствует, ставим 0
        }
        val finalSortedTracks = tracksWithTimestamp.sortedByDescending { it.timestamp }


        finalSortedTracks.forEach { track ->
        }
        emit(convertFromTrackEntity(finalSortedTracks))
    }
}