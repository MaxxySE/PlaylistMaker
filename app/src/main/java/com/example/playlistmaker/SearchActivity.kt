package com.example.playlistmaker


import android.annotation.SuppressLint
import android.opengl.Visibility
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
import com.example.playlistmaker.additional.RetrofitInit
import com.example.playlistmaker.apis.TrackApi
import com.example.playlistmaker.entities.Track
import com.example.playlistmaker.playlist_recycler.PlaylistAdapter
import com.example.playlistmaker.responses.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var inputedText = TEXT


    private val baseUrl = "https://itunes.apple.com"
    private val playlistAdapter = PlaylistAdapter()
    private val trackApi = RetrofitInit().getRetrofit(baseUrl).create(TrackApi::class.java)

    private lateinit var searchbar : EditText
    private lateinit var clearBtn : View
    private lateinit var backBtn : View
    private lateinit var searchRecycler : RecyclerView
    private lateinit var notFoundView : View
    private lateinit var noServiceView : View
    private lateinit var updateButton : Button

    private val trackList = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        clickListeners()

        setRecycler()

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
    }

    private fun setRecycler(){
        playlistAdapter.trackList = trackList
        searchRecycler.adapter = playlistAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clickListeners(){
        clearBtn.setOnClickListener {
            searchbar.text.clear()
            trackList.clear()
            playlistAdapter.notifyDataSetChanged()

            notFoundView.visibility = View.GONE
            noServiceView.visibility = View.GONE

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

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputedText = s.toString()
                clearBtn.visibility = clearButtonVisibility(s)
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