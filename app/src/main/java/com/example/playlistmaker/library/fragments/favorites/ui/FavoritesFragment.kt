package com.example.playlistmaker.library.fragments.favorites.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.library.fragments.favorites.ui.viewmodel.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel by viewModel<FavoritesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoritesBinding.bind(view)
    }

}