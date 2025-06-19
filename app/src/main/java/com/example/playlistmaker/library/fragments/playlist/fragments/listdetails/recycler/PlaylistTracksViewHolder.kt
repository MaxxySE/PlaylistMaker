package com.example.playlistmaker.library.fragments.playlist.fragments.listdetails.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistTracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val imageTrack: ImageView = itemView.findViewById(R.id.track_image)
    private val nameTrack: TextView = itemView.findViewById(R.id.track_name)
    private val artistTrack: TextView = itemView.findViewById(R.id.track_artist)
    private val timeTrack: TextView = itemView.findViewById(R.id.track_time)

    fun bind(model: Track) {
        nameTrack.text = model.trackName
        artistTrack.text = model.artistName
        timeTrack.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.dp4)))
            .into(imageTrack)
    }
}