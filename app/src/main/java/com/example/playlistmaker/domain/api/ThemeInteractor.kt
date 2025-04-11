package com.example.playlistmaker.domain.api

interface ThemeInteractor {
    fun getTheme() : Boolean
    fun setTheme(darkThemeEnabled : Boolean)
    fun switchTheme(darkThemeEnabled : Boolean)
    fun initTheme()
}