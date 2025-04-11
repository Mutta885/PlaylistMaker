package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SongsRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import java.util.concurrent.Executors

class TracksInteractorImpl (private val repository: SongsRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                consumer.consume(repository.searchSongs(expression))
            }
            catch(t: Throwable){
                consumer.onFailure(t)
            }
        }
    }
}