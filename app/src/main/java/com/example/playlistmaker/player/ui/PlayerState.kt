package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.models.Track

sealed class PlayerState {

    data class Loading (val track : Track) : PlayerState()
    class Paused(val timer : String) : PlayerState()
    class Playing(val timer : String) : PlayerState()
    class Prepared(val timer : String) : PlayerState()
}


