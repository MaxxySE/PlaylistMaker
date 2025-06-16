package com.example.playlistmaker.library.fragments.playlist.ui.recyler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.library.fragments.playlist.domain.model.Playlist

class PlaylistGridAdapter(
    private val clickListener: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistGridViewHolder>() {

    var playlists = mutableListOf<Playlist>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistGridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_grid_item, parent, false)
        return PlaylistGridViewHolder(view)
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistGridViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            clickListener(playlist)
        }
    }
}