package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.SongsRepositoryImpl
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.api.SongsRepository
import com.example.playlistmaker.settings.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.SharingRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<SongsRepository>{ SongsRepositoryImpl(get()) }

    single<ThemeRepository>{ ThemeRepositoryImpl(get(),get()) }

    single<HistoryRepository>{ HistoryRepositoryImpl(get()) }

    factory<PlayerRepository>{ PlayerRepositoryImpl(get(),get()) }

    single<SharingRepository>{ SharingRepositoryImpl(get()) }

}