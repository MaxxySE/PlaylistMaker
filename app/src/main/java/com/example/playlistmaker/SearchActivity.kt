package com.example.playlistmaker


import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    var inputedText = TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchbar : EditText = findViewById(R.id.search_edittext)
        val clearBtn : View = findViewById(R.id.clear_btn)
        val backBtn : View = findViewById(R.id.back_btn)

        clearBtn.setOnClickListener {
            searchbar.text.clear()
        }

        backBtn.setOnClickListener {
            this.finish()
        }

        if (savedInstanceState != null) {
            inputedText = savedInstanceState.getString(INPUTED_TEXT, TEXT)
            searchbar.setText(inputedText)
        }

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

        searchbar.addTextChangedListener(textWatcher)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUTED_TEXT, inputedText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputedText = savedInstanceState.getString(INPUTED_TEXT, TEXT)
    }

    companion object {
        const val INPUTED_TEXT = "INPUTED_TEXT"
        const val TEXT = ""
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

}