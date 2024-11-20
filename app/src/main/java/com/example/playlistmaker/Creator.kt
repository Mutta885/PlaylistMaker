package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SongsRepositoryImpl
import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.HistoryRepository
import com.example.playlistmaker.domain.api.SongsRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private lateinit var application: Application

    fun initApplication(app: Application){
        application = app
    }

    private fun getSongsRepository(): SongsRepository {
        return SongsRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getSongsRepository())
    }

    private fun getThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(application)
    }
    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }

    private fun getHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl(application)
    }
    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

}