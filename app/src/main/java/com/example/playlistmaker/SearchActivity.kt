package com.example.playlistmaker


import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.entities.Track
import com.example.playlistmaker.playlist_recycler.PlaylistAdapter

class SearchActivity : AppCompatActivity() {

    private var inputedText = TEXT

    private lateinit var searchbar : EditText
    private lateinit var clearBtn : View
    private lateinit var backBtn : View
    private lateinit var searchRecycler : RecyclerView

    private val trackList = listOf(
        Track("Smells Like Teen Spirit"
            , "Nirvana"
            , "5:01"
            , "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
        Track("Billie Jean"
            , "Michael Jackson"
            , "4:35"
            , "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
        Track("Stayin' Alive"
            , "Bee Gees"
            , "4:10"
            , "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
        Track("Whole Lotta Love"
            , "Led Zeppelin"
            , "5:33"
            , "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
        Track("Sweet Child O'Mine"
            , "Guns N' Roses"
            , "5:03"
            , "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"),
        Track("Long long long very long name thats cant fit on the screen, at least i think so"
            , "Maxxy"
            , "1029387:29"
            , "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg")
    )


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
    }

    private fun setRecycler(){
        val playlistAdapter = PlaylistAdapter(trackList)
        searchRecycler.adapter = playlistAdapter
    }

    private fun clickListeners(){
        clearBtn.setOnClickListener {
            searchbar.text.clear()

            val view: View? = this.currentFocus

            view?.let { v -> hideKeyboard(v) }

        }

        backBtn.setOnClickListener {
            this.finish()
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