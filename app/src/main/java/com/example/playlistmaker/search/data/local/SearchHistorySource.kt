package com.example.playlistmaker.search.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.HistoryTrackDto
import com.example.playlistmaker.sharing.data.settings.ConstData
import com.example.playlistmaker.sharing.domain.models.Track
import com.google.gson.Gson

class SearchHistorySource(
    private val sharedPreferences: SharedPreferences
) {
    private val constData = ConstData()
    private val searchKey = constData.getSearchHistoryKey()

    fun saveTrackList(historyList: List<HistoryTrackDto>) {
        val json = Gson().toJson(historyList)
        sharedPreferences.edit()
            .putString(searchKey, json)
            .apply()
    }

    fun readTrackList(): List<HistoryTrackDto> {
        val json = sharedPreferences.getString(searchKey, null) ?: return emptyList()
        return Gson().fromJson(json, Array<HistoryTrackDto>::class.java).toList()
    }

    fun clear() {
        sharedPreferences.edit()
            .remove(searchKey)
            .apply()
    }
}