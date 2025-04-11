package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SongsRepository {
    fun searchSongs(expression: String): List<Track>
}