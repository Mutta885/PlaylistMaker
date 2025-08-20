package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.domain.models.TrackWithOrder
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface PlaylistsInteractor {
    suspend fun savePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun addToPlaylist(track: Track, playlist: Playlist)
    suspend fun removeTrackFromPlaylist(trackId: Int, playlist: Playlist): List<TrackWithOrder>
    suspend fun getPlaylistById(id: Int): Playlist
}