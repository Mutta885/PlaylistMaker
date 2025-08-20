package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.dto.SongsResponse
import com.example.playlistmaker.search.data.dto.SongsSearchRequest
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.api.SongsRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.playlistmaker.library.data.toDomain

class SongsRepositoryImpl(private val networkClient: NetworkClient) : SongsRepository {

    override fun searchSongs(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(SongsSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                emit (Resource.Success ((response as SongsResponse).results.map {

                    it.toDomain()}))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера")) //emptyList()
            }
        }
    }
}
