package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SongsRepository {
    fun searchSongs(expression: String): List<Track>
}