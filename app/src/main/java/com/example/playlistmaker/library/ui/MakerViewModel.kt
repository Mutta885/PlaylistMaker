package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.PlaylistsInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch



class MakerViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<CreatingPlaylistState>()
    fun observeState(): LiveData<CreatingPlaylistState> = stateLiveData


    fun savePlaylist(playlist: Playlist) {
        stateLiveData.postValue(CreatingPlaylistState.Loading)
        viewModelScope.launch {
            playlistsInteractor.savePlaylist(playlist)
            stateLiveData.postValue(CreatingPlaylistState.Success(playlist.name))
        }
    }

    sealed interface CreatingPlaylistState {
        data object Loading : CreatingPlaylistState
        data class Success(
            val name: String
        ) : CreatingPlaylistState

    }

}