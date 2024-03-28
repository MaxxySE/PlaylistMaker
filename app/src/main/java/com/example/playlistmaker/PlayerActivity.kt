package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.entities.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerCover : ImageView
    private lateinit var backBtn : View
    private lateinit var playerName : TextView
    private lateinit var playerArtist : TextView
    private lateinit var trackLength : TextView
    private lateinit var trackAlbum : TextView
    private lateinit var trackYear : TextView
    private lateinit var trackGenre : TextView
    private lateinit var trackCountry : TextView
    private lateinit var albumFrame : View

    private lateinit var track : Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initElements()
        setPlayer()
        listeners()

    }

    private fun initElements(){
        playerCover = findViewById(R.id.player_cover)
        backBtn = findViewById(R.id.back_btn)
        playerName = findViewById(R.id.player_name)
        playerArtist = findViewById(R.id.player_artist)
        trackLength = findViewById(R.id.track_length)
        trackAlbum = findViewById(R.id.track_album)
        trackYear = findViewById(R.id.track_year)
        trackGenre = findViewById(R.id.track_genre)
        trackCountry = findViewById(R.id.track_country)
        albumFrame = findViewById(R.id.album_frame)

        //не знаю может можно другим способом.
        track = intent.getSerializableExtra("track") as Track
    }

    private fun setPlayer(){

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.album_placeholder)
            .fitCenter()
            .transform(RoundedCorners(20))
            .into(playerCover)

        playerName.text = track.trackName
        playerArtist.text = track.artistName
        trackLength.text = track.getTrackTime()

        if (track.collectionName != null){
            trackAlbum.text = track.collectionName
            albumFrame.visibility = View.VISIBLE
        } else {
            albumFrame.visibility = View.GONE
        }

        trackYear.text = track.getYear()
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country

    }

    private fun listeners(){

        backBtn.setOnClickListener {
            this.finish()
        }

    }

}