package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch


class SearchViewModel(private val searchHistory : HistoryInteractor,
                      private val tracksInteractor : TracksInteractor) : ViewModel() {

    private var searchStateLiveDataMutable = MutableLiveData<SearchState>()
    val searchStateLiveData : LiveData<SearchState> = searchStateLiveDataMutable

    init {
        searchStateLiveDataMutable.postValue(SearchState.History(getHistory()))
    }

    private fun getHistory() : List<Track> {
        return searchHistory.getHistory()
    }

    fun toHistory(track : Track) {
        searchHistory.addTrackToHistory(track)
        searchStateLiveDataMutable.postValue(SearchState.History(getHistory()))
    }

    fun clearHistory(){
        searchHistory.clearHistory()
        searchStateLiveDataMutable.postValue(SearchState.History(getHistory()))
    }


    fun search(inputStr : String){

        viewModelScope.launch {
            tracksInteractor
                .searchTracks(inputStr)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }

    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {

        when {

            foundTracks.isNullOrEmpty() -> {
                searchStateLiveDataMutable.postValue((SearchState.Nothing))
            }

            errorMessage != null -> {
                searchStateLiveDataMutable.postValue(SearchState.Error(errorMessage))
            }

            else -> {
                searchStateLiveDataMutable.postValue(SearchState.Success(foundTracks))
            }
        }
    }


}