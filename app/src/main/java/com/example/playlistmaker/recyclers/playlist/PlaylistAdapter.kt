package com.example.playlistmaker.recyclers.playlist

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.LibraryActivity
import com.example.playlistmaker.MainActivity
import com.example.playlistmaker.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.additional.ConstData
import com.example.playlistmaker.additional.SearchHistory
import com.example.playlistmaker.entities.Track

class PlaylistAdapter(private val context : Context) : RecyclerView.Adapter<PlaylistViewHolder>(){

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    var trackList : MutableList<Track> = mutableListOf()
    val searchHistory = SearchHistory(
        context.getSharedPreferences(ConstData().getPlaylistPref(), Application.MODE_PRIVATE
    ))


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int = trackList.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()){
                val playerIntent = Intent(context, PlayerActivity::class.java)
                playerIntent.putExtra("track", trackList[position])
                context.startActivity(playerIntent)
                searchHistory.saveTrackToList(trackList[position])
            }
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}