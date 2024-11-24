package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.SongsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface iTunesApi {
    @GET("/search?entity=song")
    fun searchSongs(@Query("term") text: String) : Call<SongsResponse>
}

