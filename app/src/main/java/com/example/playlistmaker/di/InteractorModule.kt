package com.example.playlistmaker.di

import com.example.playlistmaker.library.data.StrContextImpl
import com.example.playlistmaker.library.domain.StrContext
import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.domain.db.PlaylistsInteractor
import com.example.playlistmaker.library.domain.impl.FavoritesInteractorImpl
import com.example.playlistmaker.library.domain.impl.PlaylistsInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }

    factory<HistoryInteractor> { HistoryInteractorImpl(get()) }

    factory<TracksInteractor> { TracksInteractorImpl(get()) }

    factory<ThemeInteractor> { ThemeInteractorImpl(get()) }

    factory<SharingInteractor> { SharingInteractorImpl(get()) }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }

    single<StrContext> {
        StrContextImpl(
            context = get()
        )
    }


}

