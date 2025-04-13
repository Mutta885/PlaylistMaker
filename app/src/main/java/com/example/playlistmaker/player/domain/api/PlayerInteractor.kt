package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface PlayerInteractor {

    fun preparePlayer(onCompleted:()->Unit)
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl()
    fun getCrrntTrack(): Track
    fun getPlayBttnState():Boolean
    fun destroyPlayer()
    fun getPlayerState(): Int
    fun getMediaPlayerTimer(): Int
}