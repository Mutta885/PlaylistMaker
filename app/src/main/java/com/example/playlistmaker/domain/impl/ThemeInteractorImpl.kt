package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeInteractorImpl (private val repository : ThemeRepository) : ThemeInteractor {

   override fun getTheme(): Boolean {
        return repository.getTheme()
   }

   override fun setTheme(darkThemeEnabled: Boolean) {
        return repository.setTheme(darkThemeEnabled)
   }

   override fun switchTheme(darkThemeEnabled: Boolean) {
       return repository.switchTheme(darkThemeEnabled)
   }

    override fun initTheme() {
        return repository.initTheme()
    }

}