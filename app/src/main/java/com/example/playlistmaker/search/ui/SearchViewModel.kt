package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track


class SearchViewModel : ViewModel() {

    private val searchHistory = Creator.provideHistoryInteractor()
    private val tracksInteractor = Creator.provideTracksInteractor()

    private var searchStateLiveDataMutable = MutableLiveData<SearchState>()
    val searchStateLiveData : LiveData<SearchState> = searchStateLiveDataMutable

    fun getHistory() : List<Track> {
        return searchHistory.getHistory()
    }

    fun toHistory(track : Track) {
        searchHistory.addTrackToHistory(track)
    }

    fun clearHistory(){
        searchHistory.clearHistory()
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