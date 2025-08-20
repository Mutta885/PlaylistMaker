package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.domain.StrContext
import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.domain.db.PlaylistsInteractor
import com.example.playlistmaker.search.domain.models.Track
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.TrackWithOrder
import kotlinx.coroutines.launch

class PlaylistScreenViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val str: StrContext
) : ViewModel() {

    private var _playlist: Playlist? = null
    private val playlist: Playlist get() = requireNotNull(_playlist) { "Playlist wasn't initialized" }

    private val tracksLiveData = MutableLiveData<TracksState>()
    fun getTracksLiveData(): LiveData<TracksState> = tracksLiveData

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun getPlaylistLiveData(): LiveData<Playlist> = playlistLiveData

    private val navigateUpEvent = MutableLiveData<Unit>()
    val getNavigateUpEvent: LiveData<Unit> get() = navigateUpEvent


    private fun getTracks(tracks: List<TrackWithOrder>) {
        viewModelScope.launch {
            favoritesInteractor.getTracksByIds(tracks).collect { tracks ->
                processTracksRequestResult(tracks)
            }
        }
    }


    private fun processTracksRequestResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            tracksLiveData.value = TracksState.Empty
        } else {
            tracksLiveData.value = TracksState.Content(tracks)
        }
    }


    fun removeTrackFromPlaylist(trackId: Int) {
        viewModelScope.launch {
            playlistsInteractor.removeTrackFromPlaylist(trackId, playlist)
            updatePlaylist(playlist.id)
        }
    }

    fun updatePlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)
            _playlist = playlist
            playlistLiveData.value = playlist
            getTracks(playlist.tracks)
        }
    }


    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlist)
            navigateUpEvent.postValue(Unit)
        }
    }

    fun getTotalDurationText(tracks: List<Track>): String {
        var totalDuration  = 0
        tracks.forEach { track ->
            totalDuration += track.trackTimeMillis
        }
        totalDuration /= 60000

        val minutesEnding = when {
            totalDuration % 100 in 11..19 -> ""
            totalDuration % 10 == 1 -> MINUTE_ENDING_A
            totalDuration % 10 in 2..4 -> MINUTE_ENDING_I
            else -> ""
        }

        return str.getString(
            R.string.total_time,
            totalDuration,
            minutesEnding
        )
    }

    fun getQuantityText(numbers: Int): String {
        return str.getString(
            R.string.number_of_tracks,
            numbers,
            getTracksEnding(numbers)
        )
    }

    private fun getTracksEnding(quantity: Int): String {
        val remainder10 : Int = quantity % 10
        val remainder100 = quantity % 100
        val trackEnding =
            when {
                remainder100 in 11..19 -> TREK_ENDING_OV
                remainder10 == 1 -> ""
                remainder10 in 2..4 -> TREK_ENDING_A
                else -> TREK_ENDING_OV
            }
        return trackEnding
    }



    sealed interface TracksState {
        data class Content(
            val tracks: List<Track>
        ) : TracksState

        data object Empty : TracksState
    }

    companion object {
        const val MINUTE_ENDING_A = "а"
        const val MINUTE_ENDING_I = "ы"
        const val TREK_ENDING_OV = "ов"
        const val TREK_ENDING_A = "а"
    }



}