package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


private const val HISTORY_MAX_SIZE = 10

class SearchHistory (sharedPrefs : SharedPreferences){

    private val sharedPreferences = sharedPrefs
    var historyList : ArrayList<Track> = arrayListOf()

    private var gson = Gson()

    init {
        updateHistory()
    }

    fun addTrackToHistory(track : Track){

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

    fun clearHistory(){
        historyList.clear()
        syncHistory()
    }

    fun updateHistory() {
        val jsonString = sharedPreferences.getString(HISTORY_LIST, null)
        if (jsonString != null) {
            val typeToken = object : TypeToken<ArrayList<Track>>() {}.type
            historyList = gson.fromJson(jsonString, typeToken)
        }
    }

    fun syncHistory(){
        val json = gson.toJson(historyList)
        sharedPreferences.edit()
            .putString(HISTORY_LIST, json)
            .apply()
    }

}