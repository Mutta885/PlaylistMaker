package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(sharedPreferences: SharedPreferences, isSystemDarkTheme: Boolean) : ThemeRepository {

    private companion object {
        const val DARK_THEME_KEY = "dark_theme_key"
    }

    private val sharedPrefs = sharedPreferences
    private var darkTheme = false
    private val sysTheme = isSystemDarkTheme


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
        if (sharedPrefs.contains(DARK_THEME_KEY)){
            switchTheme(getTheme())
        } else {
            switchTheme(sysTheme)
            setTheme(sysTheme)
        }
    }


}