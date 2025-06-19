package com.example.playlistmaker.library.fragments.playlist.fragments.listdetails

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.library.fragments.playlist.fragments.listdetails.recycler.PlaylistTracksAdapter
import com.example.playlistmaker.library.fragments.playlist.fragments.listdetails.viewmodel.PlaylistDetailsScreenState
import com.example.playlistmaker.library.fragments.playlist.fragments.listdetails.viewmodel.PlaylistDetailsViewModel
import com.example.playlistmaker.sharing.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsFragment : Fragment(R.layout.fragment_playlist_details) {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistDetailsViewModel by viewModel()
    private var tracksAdapter: PlaylistTracksAdapter? = null

    private lateinit var tracksBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBehavior: BottomSheetBehavior<LinearLayout>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistDetailsBinding.bind(view)

        setupAdapter()
        setupBottomSheet()
        setupClickListeners()
        setupViewModelObservers()
    }

    private fun setupAdapter() {
        tracksAdapter = PlaylistTracksAdapter(
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.action_playlistDetailsFragment_to_playerActivity,
                    bundleOf("track" to track)
                )
            },
            onTrackLongClick = { track ->
                showDeleteConfirmationDialog(track)
            }
        )
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = tracksAdapter
    }

    private fun setupBottomSheet() {
        tracksBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)
        tracksBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        menuBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)
        menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val overlay = binding.overlay
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.visibility = if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = if (slideOffset > 0) slideOffset else 0f
            }
        }
        menuBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }

        binding.shareBtn.setOnClickListener { viewModel.onSharePlaylistClicked() }

        binding.moreBtn.setOnClickListener { menuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }

        binding.tvMenuShare.setOnClickListener { viewModel.onSharePlaylistClicked() }
        binding.tvMenuDelete.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistConfirmationDialog()
        }

        binding.tvMenuEdit.setOnClickListener {
            (viewModel.screenState.value as? PlaylistDetailsScreenState.Content)?.let { state ->
                findNavController().navigate(
                    R.id.action_playlistDetailsFragment_to_playlistEditFragment,
                    bundleOf("playlist" to state.details.playlist)
                )
            }
        }
    }

    private fun setupViewModelObservers() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
        viewModel.sharePlaylistEvent.observe(viewLifecycleOwner) { content ->
            if (content == null) {
                Toast.makeText(requireContext(), "В этом плейлисте нет списка треков, которым можно поделиться", Toast.LENGTH_SHORT).show()
            } else {
                sharePlaylist(content)
            }
        }
        viewModel.closeScreenEvent.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun render(state: PlaylistDetailsScreenState) {
        when (state) {
            is PlaylistDetailsScreenState.Content -> {
                val playlist = state.details.playlist
                val tracks = state.details.tracks

                binding.listTitle.text = playlist.name
                binding.listDescription.text = playlist.description.takeIf { !it.isNullOrEmpty() } ?: ""
                binding.listDescription.visibility = if (playlist.description.isNullOrEmpty()) View.GONE else View.VISIBLE

                binding.listAmount.text = resources.getQuantityString(
                    R.plurals.track_count_plurals, playlist.trackCount, playlist.trackCount
                )
                val totalDuration = tracks.sumOf { it.trackTimeMillis }
                val totalMinutes = SimpleDateFormat("mm", Locale.getDefault()).format(totalDuration).toInt()
                binding.listDuration.text = resources.getQuantityString(
                    R.plurals.duration_count_plurals, totalMinutes, totalMinutes
                )
                Glide.with(this).load(playlist.imageUri).placeholder(R.drawable.placeholder).centerCrop().into(binding.mySquareImage)

                binding.tvMenuPlaylistName.text = playlist.name
                binding.tvMenuTrackCount.text = binding.listAmount.text // Используем уже готовую строку
                Glide.with(this).load(playlist.imageUri).placeholder(R.drawable.placeholder).centerCrop().transform(
                    RoundedCorners(resources.getDimensionPixelSize(R.dimen.dp4))
                ).into(binding.ivMenuPlaylistCover)

                tracksAdapter?.tracks = tracks.toMutableList()
                tracksAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun showDeleteConfirmationDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setNegativeButton("НЕТ") { _, _ -> }
            .setPositiveButton("ДА") { _, _ -> viewModel.deleteTrack(track) }
            .show()
    }

    private fun showDeletePlaylistConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист «${binding.listTitle.text}»?")
            .setNegativeButton("Нет") { _, _ -> }
            .setPositiveButton("Да") { _, _ -> viewModel.onDeletePlaylistConfirmed() }
            .show()
    }

    private fun sharePlaylist(content: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
        }
        startActivity(Intent.createChooser(intent, "Поделиться плейлистом"))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        tracksAdapter = null
    }
}
