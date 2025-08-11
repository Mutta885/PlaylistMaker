package com.example.playlistmaker.library.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "track_table")
data class TrackEntity (
    @PrimaryKey
    val trackId: Long,              // Идентификатор трека
    val trackName: String,          // Название композиции
    val artistName: String,         // Имя исполнителя
    val trackTime: Long,            // Продолжительность трека
    val artworkUrl100: String,      // Ссылка на изображение обложки
    val collectionName: String,     // Название альбома
    val releaseDate: String,        // Год релиза трека
    val primaryGenreName: String,   // Жанр трека
    val country: String,            // Страна исполнителя
    val previewUrl : String,        // Ссылка на отрывок трека
    val additionDate : Long = System.currentTimeMillis()

)
