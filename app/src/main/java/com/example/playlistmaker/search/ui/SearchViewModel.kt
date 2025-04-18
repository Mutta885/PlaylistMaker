package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track


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

        tracksInteractor.searchTracks(inputStr,
            object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                        if (foundTracks.isNotEmpty()) {
                            searchStateLiveDataMutable.postValue(SearchState.Success(foundTracks))
                        } else {
                            searchStateLiveDataMutable.postValue((SearchState.Nothing))
                        }
                }
                override fun onFailure(t: Throwable) {
                        searchStateLiveDataMutable.postValue(SearchState.Error(t))
                }
            })

    }

}