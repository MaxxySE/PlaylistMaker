package com.example.playlistmaker.library.fragments.playlist.fragments.editplaylist

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.fragments.playlist.domain.model.Playlist
import com.example.playlistmaker.library.fragments.playlist.fragments.creation.CreationFragment
import com.example.playlistmaker.library.fragments.playlist.fragments.editplaylist.viewmodels.PlaylistEditViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistEditFragment : CreationFragment() {

    override val viewModel: PlaylistEditViewModel by viewModel()

    private lateinit var playlist: Playlist

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedPlaylist = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable("playlist", Playlist::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable("playlist")
        }

        if (receivedPlaylist == null) {
            findNavController().popBackStack()
            return
        }
        playlist = receivedPlaylist

        viewModel.init(playlist)
        bindViews(playlist)
    }

    private fun bindViews(playlist: Playlist) {
        binding.screenTitle.text = getString(R.string.edit)
        binding.saveButton.text = getString(R.string.save)

        binding.titleField.setText(playlist.name)
        binding.editTextDescription.setText(playlist.description)

        Glide.with(this)
            .load(playlist.imageUri)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.dp8)))
            .into(binding.imagePreview)

        if (!playlist.imageUri.isNullOrEmpty()) {
            binding.imagePlaceholder.visibility = View.GONE
            binding.imagePreview.visibility = View.VISIBLE
        }
    }

    override fun setupClickListeners() {
        super.setupClickListeners()

        binding.saveButton.setOnClickListener {
            viewModel.updatePlaylist(
                name = binding.titleField.text.toString(),
                description = binding.editTextDescription.text.toString()
            )
        }
    }

    override fun setupViewModelObservers() {
        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                binding.imagePlaceholder.visibility = View.GONE
                binding.imagePreview.visibility = View.VISIBLE
                Glide.with(this)
                    .load(uri)
                    .into(binding.imagePreview)
            } else {
                binding.imagePlaceholder.visibility = View.VISIBLE
                binding.imagePreview.visibility = View.GONE
            }
        }

        viewModel.playlistCreated.observe(viewLifecycleOwner) { playlistName ->
            Toast.makeText(requireContext(), "Плейлист $playlistName сохранен", Toast.LENGTH_SHORT).show()
            closeScreen()
        }
    }

    override fun handleBackPress() {
        findNavController().popBackStack()
    }
}