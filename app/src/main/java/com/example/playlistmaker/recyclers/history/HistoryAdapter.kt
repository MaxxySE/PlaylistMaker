package com.example.playlistmaker.recyclers.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.entities.Track
import com.example.playlistmaker.recyclers.playlist.PlaylistViewHolder

class HistoryAdapter : RecyclerView.Adapter<PlaylistViewHolder>() {

    var historyTrackList : MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyTrackList.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(historyTrackList[position])
    }
}