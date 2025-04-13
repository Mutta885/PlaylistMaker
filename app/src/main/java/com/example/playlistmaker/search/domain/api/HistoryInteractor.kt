package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface HistoryInteractor {
    fun addTrackToHistory(track : Track)
    fun clearHistory()
    fun updateHistory()
    fun getHistory(): List<Track>
}