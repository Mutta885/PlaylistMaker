package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun addTrackToHistory(track : Track)
    fun clearHistory()
    fun updateHistory()
    fun getHistory(): List<Track>
}