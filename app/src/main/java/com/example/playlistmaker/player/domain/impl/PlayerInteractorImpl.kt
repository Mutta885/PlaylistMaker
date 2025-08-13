package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.domain.models.Track


class PlayerInteractorImpl(private val repository : PlayerRepository) : PlayerInteractor {

    override fun preparePlayer(url : String, onCompleted:()->Unit){
        return repository.preparePlayer(url, onCompleted)
    }
    override fun startPlayer(){
        return repository.startPlayer()
    }
    override fun pausePlayer(){
        return repository.pausePlayer()
    }
    override fun playbackControl(){
        return repository.playbackControl()
    }


    override fun getPlayBttnState(): Boolean {
        return repository.getPlayBttnState()
    }

    override fun destroyPlayer() {
        return repository.destroyPlayer()
    }

    override fun getPlayerState(): Int {
        return repository.getPlayerState()
    }

    override fun getMediaPlayerTimer(): Int {
        return repository.getMediaPlayerTimer()
    }


}