package com.example.playlistmaker.search.ui.recyclers.history

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.sharing.domain.models.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.recyclers.playlist.PlaylistViewHolder

class HistoryAdapter(
    private val context: Context,
    private val historyInteractor: HistoryInteractor
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    var historyTrackList: MutableList<Track> = mutableListOf()

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
            if (clickDebounce()) {
                val track = historyTrackList[position]

                val playerIntent = Intent(context, PlayerActivity::class.java)
                playerIntent.putExtra("track", track)
                context.startActivity(playerIntent)

                historyInteractor.saveTrack(track)

                historyTrackList.clear()
                historyTrackList.addAll(historyInteractor.getHistory())
                notifyDataSetChanged()
            }
        }
    }

    private fun clickDebounce(): Boolean {
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
