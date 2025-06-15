package com.example.playlistmaker.library.fragments.playlist.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.fragments.playlist.ui.recyler.adapter.PlaylistGridAdapter
import com.example.playlistmaker.library.fragments.playlist.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.library.fragments.playlist.ui.viewmodel.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment(R.layout.fragment_playlist) {

    companion object {
        fun newInstance() = PlaylistFragment()
    }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistViewModel>()

    private var playlistsAdapter: PlaylistGridAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistBinding.bind(view)

        setupRecyclerView()
        setupClickListeners()
        setupViewModelObservers()

        viewModel.loadPlaylists()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    private fun setupRecyclerView() {
        playlistsAdapter = PlaylistGridAdapter { playlist ->
            Toast.makeText(requireContext(), "Нажали на плейлист: ${playlist.name}", Toast.LENGTH_SHORT).show()
        }
        binding.playlistsRecyclerView.adapter = playlistsAdapter
        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun setupClickListeners() {
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_creationFragment)
        }
    }

    private fun setupViewModelObservers() {
        viewModel.playlistsState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> {
                binding.placeholderView.visibility = View.VISIBLE
                binding.playlistsRecyclerView.visibility = View.GONE
            }
            is PlaylistsState.Content -> {
                binding.placeholderView.visibility = View.GONE
                binding.playlistsRecyclerView.visibility = View.VISIBLE
                playlistsAdapter?.playlists = state.playlists.toMutableList()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        playlistsAdapter = null
    }
}