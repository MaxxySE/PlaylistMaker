package com.example.playlistmaker


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.additional.ConstData
import com.example.playlistmaker.additional.RetrofitInit
import com.example.playlistmaker.apis.TrackApi
import com.example.playlistmaker.entities.Track
import com.example.playlistmaker.recyclers.history.HistoryAdapter
import com.example.playlistmaker.recyclers.playlist.PlaylistAdapter
import com.example.playlistmaker.responses.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var inputedText = TEXT


    private val baseUrl = "https://itunes.apple.com"
    private lateinit var playlistAdapter : PlaylistAdapter
    private val historyAdapter = HistoryAdapter()
    private val trackApi = RetrofitInit().getRetrofit(baseUrl).create(TrackApi::class.java)

    private val constData = ConstData()

    private lateinit var searchbar : EditText
    private lateinit var clearBtn : View
    private lateinit var backBtn : View
    private lateinit var searchRecycler : RecyclerView
    private lateinit var notFoundView : View
    private lateinit var noServiceView : View
    private lateinit var updateButton : Button
    private lateinit var historyView : View
    private lateinit var historyRecycler : RecyclerView
    private lateinit var clearHistoryButton : Button

    private val trackList : MutableList<Track> = mutableListOf()
    private var historyTrackList : MutableList<Track> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        listeners()

        setSearchRecycler()
        setHistoryRecycler()

        searchbar.requestFocus()

        if (savedInstanceState != null) {
            inputedText = savedInstanceState.getString(INPUTED_TEXT, TEXT)
            searchbar.setText(inputedText)
        }

        searchbar.addTextChangedListener(setTextWatcher(clearBtn))

    }

    private fun initViews(){
        searchbar = findViewById(R.id.search_edittext)
        clearBtn = findViewById(R.id.clear_btn)
        backBtn = findViewById(R.id.back_btn)
        searchRecycler = findViewById(R.id.search_recycler)
        notFoundView = findViewById(R.id.not_found_view)
        noServiceView = findViewById(R.id.no_service_view)
        updateButton = findViewById(R.id.update_btn)
        historyView = findViewById(R.id.tack_history)
        clearHistoryButton = findViewById(R.id.clear_track_history)
        historyRecycler = findViewById(R.id.history_recycler)

        playlistAdapter = PlaylistAdapter(this)
    }

    private fun setSearchRecycler(){
        playlistAdapter.trackList = trackList
        searchRecycler.adapter = playlistAdapter
    }

    private fun setHistoryRecycler(){
        historyTrackList = playlistAdapter.searchHistory.getHistoryTrackList()
        historyAdapter.historyTrackList = historyTrackList
        historyRecycler.adapter = historyAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listeners(){
        clearBtn.setOnClickListener {
            searchbar.text.clear()
            trackList.clear()
            playlistAdapter.notifyDataSetChanged()

            notFoundView.visibility = View.GONE
            noServiceView.visibility = View.GONE

            if(playlistAdapter.searchHistory.getHistoryTrackList().isNotEmpty()){
                historyAdapter.historyTrackList = playlistAdapter.searchHistory.getHistoryTrackList()
                historyAdapter.notifyDataSetChanged()
                historyView.visibility = View.VISIBLE
            } else {
                historyView.visibility = View.GONE
            }

            val view: View? = this.currentFocus

            view?.let { v -> hideKeyboard(v) }

        }

        updateButton.setOnClickListener {
            search()
        }

        searchbar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        backBtn.setOnClickListener {
            this.finish()
        }

        searchbar.setOnFocusChangeListener { _, hasFocus ->
            //if trackList is not empty then -> else GONE
            if(playlistAdapter.searchHistory.getHistoryTrackList().isNotEmpty()){
                if (hasFocus && searchbar.text.isEmpty()) {
                    historyAdapter.historyTrackList = playlistAdapter.searchHistory.getHistoryTrackList()
                    historyAdapter.notifyDataSetChanged()
                    historyView.visibility = View.VISIBLE
                } else {
                    historyView.visibility = View.GONE
                }

            } else {
                historyView.visibility = View.GONE
            }

        }

        clearHistoryButton.setOnClickListener{
            historyView.visibility = View.GONE
            playlistAdapter.searchHistory.clearHistory()
        }

    }

    private fun search(){
        if (searchbar.text.isNotEmpty()){
            trackApi.search(searchbar.text.toString()).enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    if(response.code() == 200) {
                        trackList.clear()
                        if(response.body()?.results?.isNotEmpty() == true){
                            trackList.addAll(response.body()?.results!!)
                            playlistAdapter.notifyDataSetChanged()
                            notFoundView.visibility = View.GONE
                            noServiceView.visibility = View.GONE
                        }
                        if (trackList.isEmpty()){
                            trackList.clear()
                            playlistAdapter.notifyDataSetChanged()
                            notFoundView.visibility = View.VISIBLE
                        }
                    } else {
                        trackList.clear()
                        playlistAdapter.notifyDataSetChanged()
                        noServiceView.visibility = View.VISIBLE
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    trackList.clear()
                    playlistAdapter.notifyDataSetChanged()
                    noServiceView.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun setTextWatcher(clearBtn : View) : TextWatcher{

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //empty
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputedText = s.toString()
                clearBtn.visibility = clearButtonVisibility(s)
                //if trackList is not null then -> else GONE
                if(playlistAdapter.searchHistory.getHistoryTrackList().isNotEmpty()){
                    if (searchbar.hasFocus() && searchbar.text.isEmpty()) {
                        //как сделать чтобы notifyDataSetChanged() работало без этого
                        historyAdapter.historyTrackList = playlistAdapter.searchHistory.getHistoryTrackList()
                        historyAdapter.notifyDataSetChanged()
                        historyView.visibility = View.VISIBLE
                        trackList.clear()
                        playlistAdapter.notifyDataSetChanged()
                    } else {
                        historyView.visibility = View.GONE
                    }

                } else {
                    historyView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }

        return textWatcher
    }

    private fun hideKeyboard(view : View){
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUTED_TEXT, inputedText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputedText = savedInstanceState.getString(INPUTED_TEXT, TEXT)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        private const val INPUTED_TEXT = "INPUTED_TEXT"
        private const val TEXT = ""
    }

}