package com.example.playlistmaker.library.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.example.playlistmaker.library.ui.tablayout.LibraryViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        val adapter = LibraryViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        val currentTab = savedInstanceState?.getInt(CURRENT_TAB_KEY) ?: 0
        binding.viewPager.currentItem = currentTab

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.favorites_tab_title)
                1 -> getString(R.string.playlists_tab_title)
                else -> ""
            }
        }.attach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_TAB_KEY, binding.viewPager.currentItem)
    }

    companion object {
        private const val CURRENT_TAB_KEY = "CURRENT_TAB_KEY"
    }
}
