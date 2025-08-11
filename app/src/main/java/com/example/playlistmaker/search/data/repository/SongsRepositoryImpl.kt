package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.dto.SongsResponse
import com.example.playlistmaker.search.data.dto.SongsSearchRequest
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.api.SongsRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SongsRepositoryImpl(private val networkClient: NetworkClient) : SongsRepository {

    override fun searchSongs(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(SongsSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                emit (Resource.Success ((response as SongsResponse).results.map {
                    Track(it.trackId, it.trackName, it.artistName, it.trackTime, it.artworkUrl100, it.collectionName, it.releaseDate, it.primaryGenreName, it.country, it.previewUrl) }))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера")) //emptyList()
            }
        }
    }
}