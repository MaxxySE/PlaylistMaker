package com.example.playlistmaker.sharing.data.settings

object ConstData {

    const val PLAYLIST_PREFERENCES = "playlist_preferences"
    const val THEME_SWITCHER_KEY = "theme_switcher_key"
    const val SEARCH_HISTORY_KEY = "search_history_key"
    const val BASE_URL = "https://itunes.apple.com"

    fun getPlaylistPref(): String = PLAYLIST_PREFERENCES
    fun getThemeSwitchKey(): String = THEME_SWITCHER_KEY
    fun getSearchHistoryKey(): String = SEARCH_HISTORY_KEY
    fun getBaseUrl(): String = BASE_URL
}