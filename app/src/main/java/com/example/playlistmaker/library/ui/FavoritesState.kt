package com.example.playlistmaker.library.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoritesState {

    data object Empty : FavoritesState
    data object Loading : FavoritesState
    data class Content(val favorites: List<Track>) : FavoritesState
}