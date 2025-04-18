package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response

import com.example.playlistmaker.search.data.dto.SongsSearchRequest

class RetrofitNetworkClient(private val iTunesService : iTunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is SongsSearchRequest) {
            val resp = iTunesService.searchSongs(dto.expression).execute()
            val body = resp.body() ?: Response()
            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }


    }
}