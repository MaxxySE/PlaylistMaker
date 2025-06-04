package com.example.playlistmaker.library.fragments.favorites.ui.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.models.Track

class FavoriteTrackAdapter(
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<FavoriteTrackViewHolder>() {

    var tracks: MutableList<Track> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteTrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return FavoriteTrackViewHolder(view)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: FavoriteTrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClick(track)
        }
    }
}