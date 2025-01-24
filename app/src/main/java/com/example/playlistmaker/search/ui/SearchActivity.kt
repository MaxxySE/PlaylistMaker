package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.recyclers.history.HistoryAdapter
import com.example.playlistmaker.search.ui.recyclers.playlist.PlaylistAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.sharing.data.dto.toDto
import com.example.playlistmaker.sharing.domain.models.Track

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private val viewModel: SearchViewModel by viewModels {
        Creator.provideSearchViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            onItemClick = { track -> navigateToPlayer(track) },
            saveHistory = { track -> viewModel.saveTrackToHistory(track) }
        )
        historyAdapter = HistoryAdapter(
            onItemClick = { track -> navigateToPlayer(track) },
            saveHistory = { track -> viewModel.saveTrackToHistory(track) }
        )
    }
    private fun setupRecyclerViews() {
        binding.searchRecycler.adapter = playlistAdapter
        binding.historyRecycler.adapter = historyAdapter
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

        binding.backBtn.setOnClickListener { finish() }

        binding.searchEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEdittext.text.isEmpty() && historyAdapter.historyTrackList.isNotEmpty()) {
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
        viewModel.state.observe(this, Observer { state ->
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

                    if (state.history.isNotEmpty()) {
                        binding.trackHistory.visibility = View.VISIBLE
                    } else {
                        binding.trackHistory.visibility = View.GONE
                    }
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
        outState.putString(INPUTED_TEXT, binding.searchEdittext.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val inputedText = savedInstanceState.getString(INPUTED_TEXT, TEXT)
        binding.searchEdittext.setText(inputedText)
    }

    private fun requestFocusAndShowKeyboard() {
        binding.searchEdittext.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEdittext, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun navigateToPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("track", track.toDto())
        }
        startActivity(intent)
    }

    companion object {
        private const val INPUTED_TEXT = "INPUTED_TEXT"
        private const val TEXT = ""
    }
}
