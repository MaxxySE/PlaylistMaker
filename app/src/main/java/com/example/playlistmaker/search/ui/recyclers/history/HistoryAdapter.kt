package com.example.playlistmaker.search.ui.recyclers.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.models.Track
import com.example.playlistmaker.search.ui.recyclers.playlist.PlaylistViewHolder

class HistoryAdapter(
    private val onItemClick: (Track) -> Unit,
    private val saveHistory: (Track) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    var historyTrackList: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int = historyTrackList.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(historyTrackList[position])
        holder.itemView.setOnClickListener {
            val track = historyTrackList[position]
            moveToFirst(track)
            saveHistory(track)
            onItemClick(track)
        }
    }

    private fun moveToFirst(track: Track) {
        historyTrackList.remove(track)
        historyTrackList.add(0, track)
        notifyDataSetChanged()
    }

    fun updateHistory(newHistory: List<Track>) {
        historyTrackList.clear()
        historyTrackList.addAll(newHistory)
        notifyDataSetChanged()
    }
}