package com.example.playlistmaker.library.fragments.playlist.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.library.fragments.playlist.fragments.creation.CreationFragment

class CreationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.creation_container, CreationFragment.newInstance())
                .commit()
        }
    }
}