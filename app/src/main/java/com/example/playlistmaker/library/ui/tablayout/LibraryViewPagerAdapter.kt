package com.example.playlistmaker.library.ui.tablayout

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.library.fragments.favorites.ui.FavoritesFragment
import com.example.playlistmaker.library.fragments.playlist.ui.PlaylistFragment
import com.example.playlistmaker.library.ui.LibraryFragment

class LibraryViewPagerAdapter(fragment: LibraryFragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment.newInstance()
            1 -> PlaylistFragment.newInstance()
            else -> throw IllegalArgumentException("Ошибка позиции окна: $position")
        }
    }
}