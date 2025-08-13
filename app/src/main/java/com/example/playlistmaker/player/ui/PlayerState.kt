package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.models.Track

sealed class PlayerState(val isPlayButtonEnabled: Boolean, val buttonDrawableSrc: String, val progress: String) {

    class Default : PlayerState(false, "play", "00:00")
    class Prepared : PlayerState(true, "play", "00:00")
    class Playing(progress: String) : PlayerState(true, "pause", progress)
    class Paused(progress: String) : PlayerState(true, "play", progress)
}

