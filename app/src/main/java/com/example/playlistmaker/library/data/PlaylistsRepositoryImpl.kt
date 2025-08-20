package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.domain.db.PlaylistsRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.domain.models.TrackWithOrder
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistsRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertOrUpdatePlaylist(playlist.toEntity())
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun addToPlaylist(track: Track, playlist: Playlist) {
        val newPlaylist = playlist.copy(
            tracks = playlist.tracks + TrackWithOrder(
                trackId = track.trackId,
                timestamp = System.currentTimeMillis()
            ),
            tracksQuantity = playlist.tracksQuantity + 1
        )

        with(appDatabase) {
            playlistDao().insertOrUpdatePlaylist(newPlaylist.toEntity())
            trackDao().addTrack(track.toEntity(System.currentTimeMillis()))
        }
    }

    override suspend fun removeTrackFromPlaylist(
        trackId: Int,
        playlist: Playlist
    ): List<TrackWithOrder> {
        val newTrackList = playlist.tracks.filter { it.trackId != trackId }
        val newPlaylist = playlist.copy(
            tracks = newTrackList,
            tracksQuantity = playlist.tracksQuantity - 1
        )

        with(appDatabase) {
            playlistDao().insertOrUpdatePlaylist(newPlaylist.toEntity())
            removeTrackFromDB(trackId)
        }
        return newTrackList
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        return appDatabase.playlistDao().getPlaylistById(id).toDomain()
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val trackList = playlist.tracks
        appDatabase.playlistDao().deletePlaylistById(playlist.id)
        trackList.forEach { track -> removeTrackFromDB(track.trackId) }
    }

    private suspend fun removeTrackFromDB(trackId: Int) {
        with(appDatabase) {
            val wasTrackFavorite = appDatabase.trackDao().checkIfTrackIsFavorite(trackId) == null
            if (wasTrackFavorite) {
                getPlaylists().collect { playlists ->
                    val isTrackInAnyPlaylist = playlists.any { playlist ->
                        playlist.tracks.any { track -> track.trackId == trackId }
                    }
                    if (!isTrackInAnyPlaylist) {
                        trackDao().deleteTracksById(trackId)
                    }
                }
            }
        }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlist.toDomain() }
    }
}



