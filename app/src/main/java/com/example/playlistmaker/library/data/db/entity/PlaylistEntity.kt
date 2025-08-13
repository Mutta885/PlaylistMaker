package com.example.playlistmaker.library.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.library.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackList: String,
    val tracksNumber: Int
)

fun PlaylistEntity.toPlaylist(): Playlist {
    return Playlist(
        id,
        name,
        description,
        coverPath,
        Gson().fromJson(trackList, object : TypeToken<List<String>>() {}.type),
        tracksNumber
    )
}