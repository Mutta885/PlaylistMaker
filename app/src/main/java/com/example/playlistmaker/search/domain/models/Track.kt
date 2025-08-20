package com.example.playlistmaker.search.domain.models


data class Track
    (
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val trackTime: String = "00:00",
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val coverArtwork: String,
    var inFavorite: Boolean = false
)