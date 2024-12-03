package com.example.playlistmaker.recyclers.history

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.additional.ConstData
import com.example.playlistmaker.additional.SearchHistory
import com.example.playlistmaker.entities.Track
import com.example.playlistmaker.recyclers.playlist.PlaylistViewHolder

class HistoryAdapter(private val context : Context) : RecyclerView.Adapter<PlaylistViewHolder>() {

    var historyTrackList : MutableList<Track> = mutableListOf()
    val searchHistory = SearchHistory(
        context.getSharedPreferences(
            ConstData().getPlaylistPref(), Application.MODE_PRIVATE
        ))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyTrackList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(historyTrackList[position])
        holder.itemView.setOnClickListener {
            val playerIntent = Intent(context, PlayerActivity::class.java)
            playerIntent.putExtra("track", historyTrackList[position])
            context.startActivity(playerIntent)
            searchHistory.saveTrackToList(historyTrackList[position])
            historyTrackList = searchHistory.getHistoryTrackList()
            this.notifyDataSetChanged()
        }
    }
}