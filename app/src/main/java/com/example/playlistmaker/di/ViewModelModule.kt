package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.FavoritesFragmentViewModel
import com.example.playlistmaker.library.ui.MakerViewModel
import com.example.playlistmaker.library.ui.PlaylistsFragmentViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        PlayerViewModel(get(),get(),get(),get())
    }

    viewModel{
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesFragmentViewModel(get())
    }

    viewModel {
        PlaylistsFragmentViewModel(get())
    }

    viewModel {
        MakerViewModel(get())
    }


}