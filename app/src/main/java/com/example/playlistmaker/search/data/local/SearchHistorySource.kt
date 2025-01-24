package com.example.playlistmaker.search.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.HistoryTrackDto
import com.example.playlistmaker.sharing.data.settings.ConstData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistorySource(
    private val sharedPreferences: SharedPreferences
) {
    private val constData = ConstData()
    private val searchKey = constData.getSearchHistoryKey()
    private val gson = Gson()

    fun saveTrackList(historyList: List<HistoryTrackDto>) {
        val json = gson.toJson(historyList)
        sharedPreferences.edit()
            .putString(searchKey, json)
            .apply()
    }

    fun readTrackList(): List<HistoryTrackDto> {
        val json = sharedPreferences.getString(searchKey, null) ?: return emptyList()
        val type = object : TypeToken<List<HistoryTrackDto>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clear() {
        sharedPreferences.edit()
            .remove(searchKey)
            .apply()
    }
}