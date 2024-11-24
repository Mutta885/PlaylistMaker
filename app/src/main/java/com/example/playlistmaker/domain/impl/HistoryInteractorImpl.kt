package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.HistoryRepository
import com.example.playlistmaker.domain.models.Track

class HistoryInteractorImpl(private val repository : HistoryRepository) : HistoryInteractor {
    override fun addTrackToHistory(track: Track) {
        return repository.addTrackToHistory(track)
    }

    override fun clearHistory() {
        return repository.clearHistory()
    }

    override fun updateHistory() {
        return repository.updateHistory()
    }

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

}