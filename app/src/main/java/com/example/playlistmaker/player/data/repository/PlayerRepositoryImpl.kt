package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.models.Track



class PlayerRepositoryImpl(private val mediaPlayer : MediaPlayer, private var historyInteractor : HistoryInteractor) : PlayerRepository {

    private enum class PlayButtonState {PLAY, PAUSE}

    private var playButtonState = PlayButtonState.PLAY

    private var playerState = STATE_DEFAULT

    private var currentTrack = historyInteractor.getHistory()[0]

    private var url : String = currentTrack.previewUrl

    override fun getCrrntTrack(): Track {
        return currentTrack
    }

    override fun getPlayBttnState():Boolean{
        if (playButtonState == PlayButtonState.PLAY) return true else return false
    }
    override fun getMediaPlayerTimer():Int{
        return mediaPlayer.currentPosition
    }

    override fun getPlayerState():Int{
        return playerState
    }

    override fun destroyPlayer() {
        if (mediaPlayer.isPlaying()) {mediaPlayer.stop()}
        mediaPlayer.release()
    }

    override fun preparePlayer(onCompleted: () -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButtonState = PlayButtonState.PLAY
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButtonState = PlayButtonState.PLAY
            playerState = STATE_PREPARED
            onCompleted()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playButtonState = PlayButtonState.PAUSE
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause()
            playButtonState = PlayButtonState.PLAY
            playerState = STATE_PAUSED
        }
    }

    override fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}