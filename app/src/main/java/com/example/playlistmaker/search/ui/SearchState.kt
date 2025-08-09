package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed class SearchState {
    object Nothing : SearchState()
    data class Success(val tracks: List<Track>) : SearchState()
    data class Error(val error: String?) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
}