package com.example.playlistmaker.library.domain.models

import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Playlist(
    val id: Int,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackList: List<String>,
    val tracksNumber: Int
)

fun Playlist.toPlaylistEntity(): PlaylistEntity {
    return PlaylistEntity(
        id,
        name,
        description,
        coverPath,
        Gson().toJson(trackList),
        tracksNumber
    )
}




