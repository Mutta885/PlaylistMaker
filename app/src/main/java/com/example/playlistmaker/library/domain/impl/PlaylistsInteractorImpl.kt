package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.PlaylistsInteractor
import com.example.playlistmaker.library.domain.db.PlaylistsRepository
import com.example.playlistmaker.library.domain.models.TrackWithOrder
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {
    override suspend fun savePlaylist(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun addToPlaylist(track: Track, playlist: Playlist) {
        repository.addToPlaylist(track, playlist)
    }

    override suspend fun removeTrackFromPlaylist(
        trackId: Int,
        playlist: Playlist
    ): List<TrackWithOrder> {
        return repository.removeTrackFromPlaylist(trackId, playlist)
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        return repository.getPlaylistById(id)
    }
}