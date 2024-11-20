package com.example.playlistmaker.data.repository

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(application: Application) : ThemeRepository {

    private companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val DARK_THEME_KEY = "dark_theme_key"
    }

    private val sharedPrefs = application.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
    private var darkTheme = false

    override fun getTheme(): Boolean {
        return sharedPrefs.getBoolean(DARK_THEME_KEY, darkTheme)
    }

    override fun setTheme(darkThemeEnabled: Boolean) {
        switchTheme(darkThemeEnabled)

        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun initTheme(){
        switchTheme(getTheme())
    }


}