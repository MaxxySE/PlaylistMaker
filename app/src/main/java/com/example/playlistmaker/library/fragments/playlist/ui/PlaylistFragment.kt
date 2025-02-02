package com.example.playlistmaker.library.fragments.playlist.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.fragments.playlist.ui.viewmodel.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistFragment : Fragment(R.layout.fragment_playlist) {

    companion object {
        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
    }

    private lateinit var binding: FragmentPlaylistBinding
    private val viewModel by viewModel<PlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlaylistBinding.bind(view)
    }

}