package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.ui.recyclers.history.HistoryAdapter
import com.example.playlistmaker.search.ui.recyclers.playlist.PlaylistAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(application, Creator.provideTrackInteractor())
    }

    private val historyInteractor by lazy { Creator.provideHistoryInteractor() }

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

        loadHistory()
        requestFocusAndShowKeyboard()
    }

    private fun initializeAdapters() {
        playlistAdapter = PlaylistAdapter(this, historyInteractor)
        historyAdapter = HistoryAdapter(this, historyInteractor)
        playlistAdapter.trackList = mutableListOf()
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
            historyInteractor.clearHistory()
            historyAdapter.historyTrackList.clear()
            historyAdapter.notifyDataSetChanged()
            binding.trackHistory.visibility = View.GONE
            Log.d("SearchActivity", "История очищена")
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
                    playlistAdapter.trackList.clear()
                    playlistAdapter.trackList.addAll(state.tracks)
                    playlistAdapter.notifyDataSetChanged()
                    hideHistoryView()
                }
                is SearchState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.noServiceView.visibility = View.VISIBLE
                    binding.searchRecycler.visibility = View.GONE
                    binding.notFoundView.visibility = View.GONE
                    hideHistoryView()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is SearchState.ShowHistory -> {
                    binding.progressBar.visibility = View.GONE
                    hideOtherViews()
                    binding.trackHistory.visibility = View.VISIBLE
                    loadHistory()
                }
            }
        })
    }

    @Suppress("NotifyDataSetChanged")
    private fun loadHistory() {
        val tracksFromHistory = historyInteractor.getHistory()
        historyAdapter.historyTrackList.clear()
        historyAdapter.historyTrackList.addAll(tracksFromHistory)

        if (historyAdapter.historyTrackList.isNotEmpty()) {
            binding.trackHistory.visibility = View.VISIBLE
            historyAdapter.notifyDataSetChanged()
            Log.d("SearchActivity", "Загружено ${historyAdapter.historyTrackList.size} треков из истории")
        } else {
            binding.trackHistory.visibility = View.GONE
            Log.d("SearchActivity", "История треков пуста")
        }
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
        loadHistory()
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

    companion object {
        private const val INPUTED_TEXT = "INPUTED_TEXT"
        private const val TEXT = ""
    }
}
