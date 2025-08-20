package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.StrContext
import com.example.playlistmaker.library.domain.db.PlaylistsInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch



class MakerViewModel(private val playlistsInteractor: PlaylistsInteractor, private val str: StrContext) : ViewModel() {

    private val stateLiveData = MutableLiveData<CreatingPlaylistState>()
    fun observeState(): LiveData<CreatingPlaylistState> = stateLiveData


    fun savePlaylist(playlist: Playlist) {
        val currentState = stateLiveData.value
        stateLiveData.postValue(CreatingPlaylistState.Loading)
        var message: String? = null

        viewModelScope.launch {
            playlistsInteractor.savePlaylist(playlist)
            if (currentState is CreatingPlaylistState.NewPlaylistState) {
                message = str.getString(R.string.created_sucks, playlist.title)
            } else if (currentState is CreatingPlaylistState.EditingPlaylist) {
                message = null
            }
            stateLiveData.postValue(CreatingPlaylistState.Success(message))
        }
    }

    fun checkIfSavedPlaylistExist(savedPlaylist: Playlist?) {
        if (savedPlaylist == null) stateLiveData.postValue(CreatingPlaylistState.NewPlaylistState)
        else stateLiveData.postValue(CreatingPlaylistState.EditingPlaylist(savedPlaylist))
    }


    sealed interface CreatingPlaylistState {
        data object Loading : CreatingPlaylistState
        data object NewPlaylistState : CreatingPlaylistState
        data class EditingPlaylist(
            val playlist: Playlist
        ) : CreatingPlaylistState
        data class Success(
            val message: String?
        ) : CreatingPlaylistState

    }

}