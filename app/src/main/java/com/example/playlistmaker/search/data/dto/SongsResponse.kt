package com.example.playlistmaker.search.data.dto

class SongsResponse (val searchType: String,
                     val expression: String,
                     val results: List<TrackDto>) : Response()

