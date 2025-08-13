package com.example.playlistmaker.library.data

import android.util.Log
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.toPlaylist
import com.example.playlistmaker.library.domain.db.PlaylistsRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.domain.models.toPlaylistEntity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.toTrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistsRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertNewPlaylist(playlist.toPlaylistEntity())
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun addToPlaylist(track: Track, playlist: Playlist) {
        val newPlaylist = playlist.copy(
            trackList = playlist.trackList + track.trackId.toString(),
            tracksNumber = playlist.tracksNumber + 1
        )

        val playlistEntity = newPlaylist.toPlaylistEntity()

        with(appDatabase) {
            playlistDao().insertNewPlaylist(playlistEntity)
            newPlaylist.trackList.forEach { track -> Log.d("Debug", " $track") }

            trackDao().insertNewFavorite(track.toTrackEntity())
        }
    }
}

private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
    return playlists.map { playlist -> playlist.toPlaylist()}
}