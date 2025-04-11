package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import java.util.Locale


class PlayerViewModel : ViewModel() {

    private val playerInteractor : PlayerInteractor = Creator.providePlayerInteractor()
    private var mainThreadHandler : Handler? = null

    private var playerStateLiveDataMutable = MutableLiveData<PlayerState>()
    val playerStateLiveData : LiveData<PlayerState> = playerStateLiveDataMutable

    init {
        mainThreadHandler = Handler(Looper.getMainLooper())
        playerStateLiveDataMutable.postValue(PlayerState.Loading(playerInteractor.getCrrntTrack()))

    }


    fun preparePlayer() {

 

        playerInteractor.preparePlayer {
            mainThreadHandler?.removeCallbacksAndMessages(null)
            playerStateLiveDataMutable.postValue(PlayerState.Prepared("00:00"))
        }

        playbackControl()

    }

    fun playbackControl(){
        playerInteractor.playbackControl()
        timerControl()

    }

    private fun timerControl(){
        when(playerInteractor.getPlayerState()) {
            STATE_PLAYING -> {
                mainThreadHandler?.post(createUpdateTimerTask())

            }
            STATE_PAUSED -> {
                mainThreadHandler?.removeCallbacks(createUpdateTimerTask())

            }

        }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {

                when (playerInteractor.getPlayBttnState()) {
                    true -> playerStateLiveDataMutable.postValue(
                        PlayerState.Paused(
                            SimpleDateFormat(
                                "mm:ss",
                                Locale.getDefault()
                            ).format(playerInteractor.getMediaPlayerTimer())
                        )
                    )
                    else -> playerStateLiveDataMutable.postValue(
                        PlayerState.Playing(
                            SimpleDateFormat(
                                "mm:ss",
                                Locale.getDefault()
                            ).format(playerInteractor.getMediaPlayerTimer())
                        )
                    )
                }

                mainThreadHandler?.postDelayed(this, DELAY)

            }
        }
    }

    fun onPause() {
        playerInteractor.pausePlayer()
    }

    fun onDestroy() {
        mainThreadHandler?.removeCallbacksAndMessages(null)
        playerInteractor.destroyPlayer()

    }


    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 500L
    }



}