package com.example.playlistmaker.additional

class ConstData {

    private val PLAYLIST_PREFERENCES = "playlist_preferences"
    private val THEME_SWITCHER_KEY = "theme_switcher_key"
    private val SEARCH_HISTORY_KEY = "search_history_key"

    fun getPlaylistPref() : String {
        return PLAYLIST_PREFERENCES
    }

    fun getThemeSwitchKey() : String {
        return THEME_SWITCHER_KEY
    }

    fun getSearchHistoryKey() : String {
        return SEARCH_HISTORY_KEY
    }

}