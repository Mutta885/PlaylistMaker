package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.network.iTunesApi
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<iTunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("PLAYLIST_MAKER_PREFERENCES", Context.MODE_PRIVATE)
    }

    single {
        androidContext()
            .resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    factory { MediaPlayer()}

}
