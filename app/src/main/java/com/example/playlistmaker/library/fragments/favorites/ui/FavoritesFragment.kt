package com.example.playlistmaker.library.fragments.favorites.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.library.fragments.favorites.FavoritesState
import com.example.playlistmaker.library.fragments.favorites.ui.recycler.FavoriteTrackAdapter
import com.example.playlistmaker.library.fragments.favorites.ui.viewmodel.FavoritesViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.sharing.domain.models.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoritesViewModel>()
    private var favoriteAdapter: FavoriteTrackAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.observe(viewLifecycleOwner) { state ->
                    renderState(state)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteTrackAdapter { track ->
            navigateToPlayer(track)
        }
        binding.favoriteRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteRecycler.adapter = favoriteAdapter
    }

    private fun renderState(state: FavoritesState) {
        binding.placeholderFavoritesLayout.isVisible = state is FavoritesState.Empty
        binding.favoriteRecycler.isVisible = state is FavoritesState.Content

        if (state is FavoritesState.Content) {
            favoriteAdapter?.tracks = state.tracks.toMutableList()
        } else if (state is FavoritesState.Empty) {
            favoriteAdapter?.tracks?.clear()
            favoriteAdapter?.notifyDataSetChanged()
        }
    }

    private fun navigateToPlayer(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("track", track)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        binding.favoriteRecycler.adapter = null
        favoriteAdapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }
}