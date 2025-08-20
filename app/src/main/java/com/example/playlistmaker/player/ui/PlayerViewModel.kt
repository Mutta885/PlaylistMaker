package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.StrContext
import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.domain.db.PlaylistsInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.PlaylistsState
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


class PlayerViewModel(private val playerInteractor : PlayerInteractor, private val favoritesInteractor: FavoritesInteractor,
                      private val playlistsInteractor: PlaylistsInteractor, private val strContext: StrContext) : ViewModel() {

    private var playerStateLiveDataMutable = MutableLiveData<PlayerState>()
    val playerStateLiveData : LiveData<PlayerState> = playerStateLiveDataMutable

    private var isFavoriteLiveDataMutable = MutableLiveData<Boolean>()
    val isFavoriteLiveData : LiveData<Boolean> = isFavoriteLiveDataMutable

    private var playlistsLiveDataMutable = MutableLiveData<PlaylistsState>()
    val PlaylistsLiveData : LiveData<PlaylistsState> = playlistsLiveDataMutable

    private var trackAddedLiveDataMutable = MutableLiveData<String>()
    val TrackAddedLiveData : LiveData<String> = trackAddedLiveDataMutable

    private var timerJob: Job? = null
    private var currentTrack: Track ? = null

    init {
       playerStateLiveDataMutable.postValue(PlayerState.Default())
    }

    fun isFavoriteState(trackId : Int){
        viewModelScope.launch{
            //isFavoriteLiveDataMutable.postValue(favoritesInteractor.isFavorite(trackId))
            val isFavorite = favoritesInteractor.checkIfTrackIsFavorite(trackId)
            isFavoriteLiveDataMutable.postValue( isFavorite)
            currentTrack = currentTrack!!.copy(inFavorite = isFavorite)

        }
    }


    fun swithFavorite() {

        val trackFavorited = currentTrack!!.inFavorite
        val newTrack = currentTrack!!.copy(inFavorite = !trackFavorited)
        //if (trackFavorited == false) {

            viewModelScope.launch {
                favoritesInteractor.changeFavorites(newTrack)

            }

        //}
        currentTrack = newTrack
        isFavoriteLiveDataMutable.postValue( !trackFavorited)

    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            playlistsLiveDataMutable.postValue(PlaylistsState.Empty)
        } else {
            playlistsLiveDataMutable.postValue(PlaylistsState.Content(playlists))
        }

    }


    fun addTrackToPlaylist(playlist: Playlist) {

        val trackIds = playlist.tracks.map { it.trackId }
        if (trackIds.contains(currentTrack!!.trackId)) {
            trackAddedLiveDataMutable.value =
                strContext.getString(
                    R.string.already_exist,
                    playlist.title
                )
        } else {

                viewModelScope.launch {
                    playlistsInteractor.addToPlaylist(currentTrack!!, playlist)
                    trackAddedLiveDataMutable.value =
                        strContext.getString(
                            R.string.added,
                            playlist.title
                        )
                    getPlaylists()
                }

        }
    }



    fun preparePlayer(track : Track) {
        currentTrack = track
        playerStateLiveDataMutable.value = PlayerState.Prepared()
        playerInteractor.preparePlayer(track.previewUrl) {

        }
        playerStateLiveDataMutable.postValue(PlayerState.Paused("00:00"))

    }

    fun onPlayButtonClicked(){
        when(playerStateLiveDataMutable.value) {
            is PlayerState.Playing -> {
                playerInteractor.pausePlayer()
                timerJob?.cancel()
                playerStateLiveDataMutable.postValue (PlayerState.Paused(
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(playerInteractor.getMediaPlayerTimer())
                )
                )
            }
            is PlayerState.Paused, is PlayerState.Prepared -> {
                playerInteractor.startPlayer()
                startTimer()

            }
            else -> {}

        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerInteractor.getPlayerState() == STATE_PLAYING) {
                delay(DELAY)
                playerStateLiveDataMutable.postValue(PlayerState.Playing(
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(playerInteractor.getMediaPlayerTimer())
                )
                )
            }
            playerStateLiveDataMutable.postValue(PlayerState.Paused("00:00"))
        }
    }

    fun onPause() {
        playerInteractor.pausePlayer()
    }

    fun onDestroy() {
        playerInteractor.destroyPlayer()
        timerJob?.cancel()
        playerStateLiveDataMutable.value = PlayerState.Default()

    }

    override fun onCleared() {
        playerInteractor.destroyPlayer()
        timerJob?.cancel()

    }

    private companion object {                  //переделать с помощью enum класса
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }



}