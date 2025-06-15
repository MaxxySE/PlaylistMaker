package com.example.playlistmaker.library.fragments.playlist.ui.recyler.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.fragments.playlist.domain.model.Playlist

class PlaylistGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cover: ImageView = itemView.findViewById(R.id.playlist_cover)
    private val name: TextView = itemView.findViewById(R.id.playlist_name)
    private val count: TextView = itemView.findViewById(R.id.playlist_track_count)

    fun bind(playlist: Playlist) {
        name.text = playlist.name

        val trackCountText = itemView.resources.getQuantityString(
            R.plurals.track_count_plurals,
            playlist.trackCount,
            playlist.trackCount
        )
        count.text = trackCountText

        Glide.with(itemView.context)
            .load(playlist.imageUri)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.dp8)))
            .into(cover)
    }
}