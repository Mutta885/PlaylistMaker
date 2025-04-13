package com.example.playlistmaker.settings.domain.api

interface ThemeRepository {
    fun getTheme() : Boolean
    fun setTheme(darkThemeEnabled : Boolean)
    fun switchTheme(darkThemeEnabled : Boolean)
    fun initTheme()
}