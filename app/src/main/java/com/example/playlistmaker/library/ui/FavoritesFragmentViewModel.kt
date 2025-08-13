package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import kotlinx.coroutines.launch

class FavoritesFragmentViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private var favoriteStateLiveDataMutable = MutableLiveData<FavoritesState>()
    val favoriteStateLiveData : LiveData<FavoritesState> = favoriteStateLiveDataMutable

    init {

        viewModelScope.launch {

            getFavorites()
        }
    }

    fun getFavorites() {
        renderState(FavoritesState.Loading)

        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect { favoriteTracks ->
                if (favoriteTracks.isEmpty()) renderState(FavoritesState.Empty)
                else renderState(FavoritesState.Content(favorites = favoriteTracks))
            }
        }
    }

    private fun renderState(favoriteState: FavoritesState) {
        favoriteStateLiveDataMutable.postValue(favoriteState)
    }



}