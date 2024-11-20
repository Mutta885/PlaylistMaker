package com.example.playlistmaker.data.repository

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.domain.api.HistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryRepositoryImpl (application: Application) : HistoryRepository {

    private companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val HISTORY_LIST = "history_list"
        const val HISTORY_MAX_SIZE = 10
    }

    private val sharedPreferences = application.getSharedPreferences(
        PLAYLIST_MAKER_PREFERENCES,
        MODE_PRIVATE
    )
    private var historyList : ArrayList<Track> = arrayListOf()

    private var gson = Gson()

    init {
        updateHistory()
    }

    override fun addTrackToHistory(track : Track){

        for (i in historyList.indices) {
            if (historyList[i].trackId == track.trackId) {
                historyList.removeAt(i)
                break
            }
        }

        historyList.add(0, track)

        if (historyList.size > HISTORY_MAX_SIZE){
            historyList.removeAt(historyList.size-1)
        }

        syncHistory()
    }

    override fun clearHistory(){
        historyList.clear()
        syncHistory()
    }

    override fun updateHistory() {
        val jsonString = sharedPreferences.getString(HISTORY_LIST, null)
        if (jsonString != null) {
            val typeToken = object : TypeToken<ArrayList<Track>>() {}.type
            historyList = gson.fromJson(jsonString, typeToken)
        }
    }

    private fun syncHistory(){
        val json = gson.toJson(historyList)
        sharedPreferences.edit()
            .putString(HISTORY_LIST, json)
            .apply()
    }

    override fun getHistory(): List<Track>{
        return historyList
    }

}