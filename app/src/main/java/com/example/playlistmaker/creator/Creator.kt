package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.MediaPlayer
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.search.data.repository.SongsRepositoryImpl
import com.example.playlistmaker.settings.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.domain.api.SongsRepository
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingRepository

object Creator {

    private lateinit var application: Application
    private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
    private var isSystemDarkTheme = false
    private lateinit var sharedPreferences: SharedPreferences

    fun initApplication(app: Application){
        application = app
        sharedPreferences = application.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES,MODE_PRIVATE)
        isSystemDarkTheme = application.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        provideThemeInteractor().initTheme()
    }

    private fun getSongsRepository(): SongsRepository {
        return SongsRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getSongsRepository())
    }

    private fun getThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(sharedPreferences, isSystemDarkTheme)
    }
    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }

    private fun getHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl(sharedPreferences)
    }
    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

    private fun getPlayerRepository(mediaPlayer : MediaPlayer): PlayerRepository {
        return PlayerRepositoryImpl(mediaPlayer)
    }
    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository(MediaPlayer()))
    }

    private fun getSharingRepository(): SharingRepository {
        return SharingRepositoryImpl(application)
    }
    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getSharingRepository())
    }


}