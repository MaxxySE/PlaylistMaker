package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.recyclers.history.HistoryAdapter
import com.example.playlistmaker.ui.search.recyclers.playlist.PlaylistAdapter
import com.example.playlistmaker.Creator
import com.example.playlistmaker.presentation.search.SearchContract
import com.example.playlistmaker.presentation.search.SearchPresenter

class SearchActivity : AppCompatActivity(), SearchContract.View {

    private lateinit var searchBar: EditText
    private lateinit var clearButton: View
    private lateinit var backButton: View
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var notFoundView: View
    private lateinit var noServiceView: View
    private lateinit var updateButton: Button
    private lateinit var historyView: View
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: View

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var presenter: SearchContract.Presenter

    private val historyInteractor by lazy {
        Creator.provideHistoryInteractor()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        presenter = SearchPresenter(Creator.provideTrackInteractor())
        presenter.attachView(this)

        initializeViews()
        setupAdapters()
        setupRecyclerViews()
        setupListeners()

        if (savedInstanceState != null) {
            val inputedText = savedInstanceState.getString(INPUTED_TEXT, TEXT)
            searchBar.setText(inputedText)
        }

        loadHistory()
        requestFocusAndShowKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun initializeViews() {
        searchBar = findViewById(R.id.search_edittext)
        clearButton = findViewById(R.id.clear_btn)
        backButton = findViewById(R.id.back_btn)
        searchRecyclerView = findViewById(R.id.search_recycler)
        notFoundView = findViewById(R.id.not_found_view)
        noServiceView = findViewById(R.id.no_service_view)
        updateButton = findViewById(R.id.update_btn)
        historyView = findViewById(R.id.track_history)
        clearHistoryButton = findViewById(R.id.clear_track_history)
        historyRecyclerView = findViewById(R.id.history_recycler)
        progressBar = findViewById(R.id.progress_bar)

        playlistAdapter = PlaylistAdapter(this, historyInteractor)
        historyAdapter = HistoryAdapter(this, historyInteractor)
    }

    private fun setupAdapters() {
        playlistAdapter.trackList = mutableListOf()
    }

    private fun setupRecyclerViews() {
        searchRecyclerView.adapter = playlistAdapter
        historyRecyclerView.adapter = historyAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupListeners() {
        clearButton.setOnClickListener {
            presenter.onClearClicked()
            searchBar.setText("")
        }

        updateButton.setOnClickListener {
            presenter.onSearchAction()
        }

        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.onSearchAction()
                true
            } else {
                false
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchBar.text.isEmpty() && historyAdapter.historyTrackList.isNotEmpty()) {
                presenter.showHistory()
            } else {
                presenter.hideHistory()
            }
        }

        clearHistoryButton.setOnClickListener {
            historyInteractor.clearHistory()
            historyAdapter.historyTrackList.clear()
            historyAdapter.notifyDataSetChanged()
            historyView.visibility = View.GONE
            Log.d("SearchActivity", "История очищена")
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                presenter.onQueryChanged(s?.toString().orEmpty())
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadHistory() {
        val tracksFromHistory = historyInteractor.getHistory()
        historyAdapter.historyTrackList.clear()
        historyAdapter.historyTrackList.addAll(tracksFromHistory)

        if (historyAdapter.historyTrackList.isNotEmpty()) {
            historyView.visibility = View.VISIBLE
            historyAdapter.notifyDataSetChanged()
            Log.d("SearchActivity", "Загружено ${historyAdapter.historyTrackList.size} треков из истории")
        } else {
            historyView.visibility = View.GONE
            Log.d("SearchActivity", "История треков пуста")
        }
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        noServiceView.visibility = View.GONE
        notFoundView.visibility = View.GONE
        searchRecyclerView.visibility = View.GONE
        hideHistory()
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
        presenter.refreshCurrentSearch()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showTracks(foundTracks: List<Track>) {
        playlistAdapter.trackList.clear()
        playlistAdapter.trackList.addAll(foundTracks)
        playlistAdapter.notifyDataSetChanged()

        searchRecyclerView.visibility = View.VISIBLE
        notFoundView.visibility = View.GONE
        noServiceView.visibility = View.GONE
        hideHistory()
    }

    override fun showNotFound() {
        notFoundView.visibility = View.VISIBLE
        searchRecyclerView.visibility = View.GONE
        hideHistory()
    }

    override fun hideNotFound() {
        notFoundView.visibility = View.GONE
    }

    override fun showNoService() {
        noServiceView.visibility = View.VISIBLE
        searchRecyclerView.visibility = View.GONE
        hideHistory()
    }

    override fun hideNoService() {
        noServiceView.visibility = View.GONE
    }

    override fun showSearchList() {
        searchRecyclerView.visibility = View.VISIBLE
    }

    override fun hideSearchList() {
        searchRecyclerView.visibility = View.GONE
    }

    override fun showHistory() {
        if (historyAdapter.historyTrackList.isNotEmpty()) {
            historyView.visibility = View.VISIBLE
            searchRecyclerView.visibility = View.GONE
        }
    }

    override fun hideHistory() {
        historyView.visibility = View.GONE
    }

    override fun enableClearButton(isEnabled: Boolean) {
        clearButton.visibility = if (isEnabled) View.VISIBLE else View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUTED_TEXT, searchBar.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val inputedText = savedInstanceState.getString(INPUTED_TEXT, TEXT)
        searchBar.setText(inputedText)
    }

    private fun requestFocusAndShowKeyboard() {
        searchBar.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
    }

    companion object {
        private const val INPUTED_TEXT = "INPUTED_TEXT"
        private const val TEXT = ""
    }
}
