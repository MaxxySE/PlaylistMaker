package com.example.playlistmaker.additional

import android.content.SharedPreferences
import com.example.playlistmaker.entities.Track
import com.google.gson.Gson



class SearchHistory (
    private val sharedPreferences: SharedPreferences
    ) {

    private val constData = ConstData()
    private var historyTrackList : MutableList<Track> = mutableListOf()
    private val searchKey = constData.getSearchHistoryKey()

    fun saveTrackToList(track: Track){
        readList()
        historyTrackList.removeAll { it.trackId == track.trackId }
        if(historyTrackList.size == 10){
            historyTrackList.removeLast()
        }
        historyTrackList.add(0, track)
        writeList()
    }

    private fun readList(){
        val json = sharedPreferences.getString(searchKey, null)
        if (json != null) {
            historyTrackList = Gson().fromJson(json, Array<Track>::class.java).toMutableList()
        }
    }

    private fun writeList(){
        val json = Gson().toJson(historyTrackList)
        sharedPreferences.edit()
            .putString(searchKey, json)
            .apply()
    }

    fun getHistoryTrackList() : MutableList<Track>{
        val json = sharedPreferences.getString(searchKey, null)
        if (json != null) {
            historyTrackList = Gson().fromJson(json, Array<Track>::class.java).toMutableList()
        }
        return historyTrackList
    }

    fun clearHistory(){
        sharedPreferences.edit()
            .remove(constData.getSearchHistoryKey())
            .apply()
    }

}