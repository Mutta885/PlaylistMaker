package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.PlaylistsInteractor
import com.example.playlistmaker.library.domain.db.PlaylistsRepository
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


    override suspend fun addToPlaylist(track: Track, playlist: Playlist) {
        repository.addToPlaylist(track, playlist)
    }
}