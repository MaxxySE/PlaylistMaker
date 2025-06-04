package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.recyclers.history.HistoryAdapter
import com.example.playlistmaker.search.ui.recyclers.playlist.PlaylistAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.sharing.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private val viewModel: SearchViewModel by viewModel()

    private var isClickAllowed = true

    companion object {
        private const val INPUTED_TEXT = "INPUTED_TEXT"
        private const val TEXT = ""
        private const val CLICK_DEBOUNCE_DELAY_MS = 1000L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        initializeAdapters()
        setupRecyclerViews()
        setupListeners()
        observeViewModel()

        if (savedInstanceState != null) {
            val inputedText = savedInstanceState.getString(INPUTED_TEXT, TEXT)
            binding.searchEdittext.setText(inputedText)
        }

        viewModel.showHistory()
        requestFocusAndShowKeyboard()
    }

    private fun initializeAdapters() {
        playlistAdapter = PlaylistAdapter(
            onItemClick = { track ->
                // Передаем действие
                clickDebounce { navigateToPlayer(track) }
            },
            saveHistory = { track -> viewModel.saveTrackToHistory(track) }
        )
        historyAdapter = HistoryAdapter(
            onItemClick = { track ->
                clickDebounce { navigateToPlayer(track) }
            },
            saveHistory = { track -> viewModel.saveTrackToHistory(track) }
        )
    }


    private fun setupRecyclerViews() {
        binding.searchRecycler.apply {
            adapter = playlistAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.historyRecycler.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.clearBtn.setOnClickListener {
            binding.searchEdittext.setText("")
            viewModel.onClearClicked()
        }

        binding.searchEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.clearBtn.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onQueryChanged(s?.toString().orEmpty())
            }
        })

        binding.updateBtn.setOnClickListener {
            viewModel.onSearchAction()
            hideKeyboard()
        }

        binding.searchEdittext.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchAction()
                hideKeyboard()
                true
            } else false
        }

        binding.searchEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus &&
                binding.searchEdittext.text.isEmpty() &&
                historyAdapter.historyTrackList.isNotEmpty()
            ) {
                viewModel.showHistory()
            } else {
                viewModel.hideHistory()
            }
        }

        binding.clearTrackHistory.setOnClickListener {
            viewModel.clearHistory()
            historyAdapter.updateHistory(emptyList())
            binding.trackHistory.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is SearchState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                }
                is SearchState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    hideOtherViews()
                }
                is SearchState.NotFound -> {
                    binding.progressBar.visibility = View.GONE
                    binding.notFoundView.visibility = View.VISIBLE
                    binding.searchRecycler.visibility = View.GONE
                    binding.noServiceView.visibility = View.GONE
                    hideHistoryView()
                }
                is SearchState.Content -> {
                    binding.progressBar.visibility = View.GONE
                    binding.notFoundView.visibility = View.GONE
                    binding.noServiceView.visibility = View.GONE
                    binding.searchRecycler.visibility = View.VISIBLE
                    playlistAdapter.updateTracks(state.tracks)
                    hideHistoryView()
                }
                is SearchState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.noServiceView.visibility = View.VISIBLE
                    binding.searchRecycler.visibility = View.GONE
                    binding.notFoundView.visibility = View.GONE
                    hideHistoryView()
                }
                is SearchState.ShowHistory -> {
                    binding.progressBar.visibility = View.GONE
                    hideOtherViews()
                    binding.trackHistory.visibility = View.VISIBLE
                    historyAdapter.updateHistory(state.history)
                    binding.trackHistory.visibility =
                        if (state.history.isNotEmpty()) View.VISIBLE else View.GONE
                }
            }
        })
    }

    private fun hideOtherViews() {
        binding.noServiceView.visibility = View.GONE
        binding.notFoundView.visibility = View.GONE
        binding.searchRecycler.visibility = View.GONE
        hideHistoryView()
    }

    private fun hideHistoryView() {
        binding.trackHistory.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        viewModel.showHistory()
        viewModel.refreshCurrentSearch()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _binding?.let {
            outState.putString(INPUTED_TEXT, it.searchEdittext.text.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            _binding?.searchEdittext?.setText(it.getString(INPUTED_TEXT, TEXT))
        }
    }

    private fun requestFocusAndShowKeyboard() {
        binding.searchEdittext.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEdittext, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun navigateToPlayer(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("track", track)
        }
        startActivity(intent)
    }

    private fun clickDebounce(action: () -> Unit) {
        if (isClickAllowed) {
            isClickAllowed = false
            action()

            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MS)
                isClickAllowed = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
