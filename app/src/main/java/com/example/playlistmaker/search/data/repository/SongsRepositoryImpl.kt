package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.dto.SongsResponse
import com.example.playlistmaker.search.data.dto.SongsSearchRequest
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.api.SongsRepository
import com.example.playlistmaker.search.domain.models.Track

class SongsRepositoryImpl(private val networkClient: NetworkClient) : SongsRepository {

    override fun searchSongs(expression: String): List<Track> {
        val response = networkClient.doRequest(SongsSearchRequest(expression))
        when (response.resultCode) {
            200 -> {
                return (response as SongsResponse).results.map {
                    Track(it.trackId, it.trackName, it.artistName, it.trackTime, it.artworkUrl100, it.collectionName, it.releaseDate, it.primaryGenreName, it.country, it.previewUrl) }
            }
            else -> {
                return emptyList()
            }
        }
    }
}